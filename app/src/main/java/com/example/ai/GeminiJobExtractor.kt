package com.example.ai

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ExtractedJobData(
    val jobTitle: String,
    val workflowMethodology: String,
    val compensation: String,
    val deliveryAndPayment: String,
    val translatedArabic: String
)

class GeminiJobExtractor {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun extractJobDetails(rawText: String, customApiKey: String? = null): ExtractedJobData = withContext(Dispatchers.IO) {
        val apiKey = when {
            !customApiKey.isNullOrBlank() -> customApiKey
            BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        if (apiKey.isEmpty()) {
            // Fallback smart parser if no API key is set yet
            return@withContext fallbackLocalExtraction(rawText)
        }

        try {
            val systemPrompt = """
                You are a structured job offer extractor. Analyze the raw Telegram job message.
                Return ONLY a JSON object without markdown code blocks, with these keys:
                1. "jobTitle": Clear concise title of the job in Arabic
                2. "workflowMethodology": Steps or methodology to perform the task in Arabic
                3. "compensation": Exact money offered or payment rate in Arabic/symbols (e.g., "$500" or "1000 ريال")
                4. "deliveryAndPayment": How to submit work and receive funds in Arabic
                5. "translatedArabic": Complete translated post into fluent Arabic
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("system_instruction", JSONObject().apply {
                    put("parts", JSONObject().apply {
                        put("text", systemPrompt)
                    })
                })
                put("contents", JSONObject().apply {
                    put("parts", JSONObject().apply {
                        put("text", "Telegram message:\n$rawText")
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.2)
                })
            }

            // Using gemini-3.5-flash endpoint
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBodyString = response.body?.string() ?: ""

            if (response.isSuccessful && responseBodyString.isNotBlank()) {
                val responseObj = JSONObject(responseBodyString)
                val candidates = responseObj.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        val cleanedText = text.replace("```json", "").replace("```", "").trim()
                        val parsedJson = JSONObject(cleanedText)

                        return@withContext ExtractedJobData(
                            jobTitle = parsedJson.optString("jobTitle", "مطلوب تنفيذ مشروع خاص"),
                            workflowMethodology = parsedJson.optString("workflowMethodology", "التواصل المباشر والاتفاق على التفاصيل"),
                            compensation = parsedJson.optString("compensation", "حسب الاتفاق"),
                            deliveryAndPayment = parsedJson.optString("deliveryAndPayment", "تسليم عبر تلغرام / دفع إلكتروني"),
                            translatedArabic = parsedJson.optString("translatedArabic", rawText)
                        )
                    }
                }
            }

            return@withContext fallbackLocalExtraction(rawText)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext fallbackLocalExtraction(rawText)
        }
    }

    private fun fallbackLocalExtraction(rawText: String): ExtractedJobData {
        val titleMatch = Regex("(?:مطلوب|Hiring|Looking for|Job|Need|We need)\\s+([^\n.]+)", RegexOption.IGNORE_CASE).find(rawText)
        val title = titleMatch?.value ?: if (rawText.length > 50) rawText.substring(0, 50) + "..." else rawText

        val budgetMatch = Regex("(\\\$|€|£|\\bUSD\\b|\\bEUR\\b|ريال|دولار|درهم|جنيه)\\s?\\d+(?:\\.\\d+)?|\\d+\\s?(\\\$|USD|ريال|دولار)", RegexOption.IGNORE_CASE).find(rawText)
        val budget = budgetMatch?.value ?: "غير محدد / قابل للتفاوض"

        val workflow = if (rawText.contains("remote", ignoreCase = true) || rawText.contains("عن بعد")) {
            "العمل عن بُعد كاملاً مع تقديم تقارير دورية"
        } else {
            "التواصل المباشر مع صاحب العمل للاتفاق على خطوات التنفيذ"
        }

        val delivery = "إرسال النماذج والأعمال المنفذة عبر الشات المباشر، والدفع فور الاعتماد"

        return ExtractedJobData(
            jobTitle = title.trim(),
            workflowMethodology = workflow,
            compensation = budget,
            deliveryAndPayment = delivery,
            translatedArabic = rawText
        )
    }
}
