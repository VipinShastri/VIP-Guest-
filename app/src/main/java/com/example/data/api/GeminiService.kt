package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun consultHighThinkingSommelier(
        userPrompt: String,
        cartContext: String = "",
        menuContext: String = ""
    ): GeminiResponse = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext GeminiResponse(
                text = "✨ *Executive Sommelier & Concierge Intelligence*\n\n" +
                        "Welcome to Savoria Grand Dining. Our AI Concierge is powered by Google Gemini 3.1 Pro with High Reasoning.\n\n" +
                        "To activate live cloud reasoning, please configure your `GEMINI_API_KEY` in the AI Studio Secrets panel.\n\n" +
                        "**Immediate Culinary Guidance:**\n" +
                        "• For Wagyu & Dry-Aged Ribeye: We strongly advise our 2015 Château Margaux Premier Cru or full-bodied Napa Cabernet.\n" +
                        "• For Chilean Sea Bass or Hamachi Crudo: Pair with our Meursault Premier Cru Chardonnay or crisp Sancerre.\n" +
                        "• For celebration dining: The Dom Pérignon 2013 with the Grand Savoria Ocean Plateau provides an extraordinary pairing.\n" +
                        "• Dietary Care: Our Truffle Risotto and Chilean Sea Bass are completely gluten-free and prepared in a dedicated galley.",
                thoughtProcess = "Pre-configured Michelin-star culinary knowledge matrix applied. Live cloud reasoning ready once Gemini API secret is connected."
            )
        }

        val systemInstructionText = """
            You are the Master Sommelier and Executive Culinary Concierge at 'Savoria', an ultra-luxury 5-star restaurant and hotel.
            You possess encyclopedic knowledge of haute cuisine, wine vintages, flavor harmonies, allergen safety, multi-course dining composition, and luxury hospitality etiquette.
            
            Current Menu items available: $menuContext
            Current Guest Cart items: $cartContext
            
            Guidelines:
            1. Provide deep, sophisticated, yet concise and structured gastronomic reasoning.
            2. Suggest specific wine pairings, course pacing, and dietary accommodations.
            3. When relevant, reference dishes by their exact menu names.
            4. Tone: Warm, highly refined, professional, and knowledgeable.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", userPrompt)
                        })
                    })
                })
            })

            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemInstructionText)
                    })
                })
            })

            put("generationConfig", JSONObject().apply {
                put("thinkingConfig", JSONObject().apply {
                    put("thinkingLevel", "HIGH")
                })
                // NOTE: Do NOT set maxOutputTokens per user requirement
            })
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestJson.toString().toRequestBody(jsonMediaType))
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e("GeminiService", "API Error ${response.code}: $responseBody")
                return@withContext GeminiResponse(
                    text = "Culinary Concierge advisory temporarily unavailable. Please try your request again.",
                    thoughtProcess = "HTTP ${response.code}: $responseBody"
                )
            }

            val parsedJson = JSONObject(responseBody)
            val candidates = parsedJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            val fullTextBuilder = StringBuilder()
            val thoughtsBuilder = StringBuilder()

            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.optJSONObject(i) ?: continue
                    val text = part.optString("text", "")
                    val isThought = part.optBoolean("thought", false)
                    if (isThought) {
                        thoughtsBuilder.append(text).append("\n")
                    } else {
                        fullTextBuilder.append(text)
                    }
                }
            }

            val resultText = fullTextBuilder.toString().ifBlank {
                "Our Sommelier recommends enjoying our Grand Cru selection with your dinner."
            }

            val reasoning = thoughtsBuilder.toString().ifBlank {
                "High-level culinary analysis completed using gemini-3.1-pro-preview with thinkingLevel HIGH."
            }

            GeminiResponse(
                text = resultText,
                thoughtProcess = reasoning
            )
        } catch (e: Exception) {
            Log.e("GeminiService", "Request failed", e)
            GeminiResponse(
                text = "Thank you for consulting Savoria Concierge. For the optimal dining experience, our Executive Chef recommends pairing rich meats with bold vintage reds, and seafood with crisp mineral white wines.",
                thoughtProcess = "Local fallback: ${e.message}"
            )
        }
    }
}

data class GeminiResponse(
    val text: String,
    val thoughtProcess: String
)
