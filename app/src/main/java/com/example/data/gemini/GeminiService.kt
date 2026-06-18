package com.example.data.gemini

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content?
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GeminiApiService by lazy {
        retrofit.create(GeminiApiService::class.java)
    }

    suspend fun getWeddingAiSuggestions(prompt: String): String {
        val keyText = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (keyText.isEmpty() || keyText == "MY_GEMINI_API_KEY") {
            return getFallbackLocalResponse(prompt)
        }
        return try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                systemInstruction = Content(parts = listOf(Part(text = "You are WedZen's expert AI Wedding Planner. Speak warmly, respectfully, and beautifully as a luxury traditional wedding architect. Provide gorgeous cultural tips, clear custom expense estimates, regional menu choices, and cost-cut solutions. Structure in gorgeous markdown with bullet points and bold headers.")))
            )
            val response = service.generateContent(keyText, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I apologize! My stars are slightly out of alignment. Please ask again in a moment."
        } catch (e: Exception) {
            getFallbackLocalResponse(prompt)
        }
    }

    private fun getFallbackLocalResponse(prompt: String): String {
        val lowerPrompt = prompt.lowercase()
        return when {
            lowerPrompt.contains("menu") || lowerPrompt.contains("food") || lowerPrompt.contains("catering") -> """
                🌸 **WedZen AI Catering Inspirations** 🌸
                
                For your premium Indian WedZen planner, a luxury culinary menu is key:
                *   **Welcome Sips**: Fresh tender coconut with cardamom, rose lassi with saffron threads.
                *   **Grand Appetizers**: Live paneer tikka counters, golden crispy coin jalebis, authentic pani puri with 5 flavorful waters (teekha, khatta, meetha, mint, hing).
                *   **Main Course Delicacies**: Rich slow-cooked Dum Biryani, Shahi Malai Kofta in ivory cashew gravy, freshly baked butter garlic naans, Dal Makhani slow-cooked for 24 hours.
                *   **Dessert Wonderland**: Warm saffron rabdi with hot syrup gulab jamuns, classic artisanal mango kulfi with silver vark.
                
                💡 *Cost Saving Tip*: Keeping live counters limited to 4 signature cuisines keeps quality unmatched while saving up to 15% on waste!
            """.trimIndent()
            
            lowerPrompt.contains("theme") || lowerPrompt.contains("decor") || lowerPrompt.contains("hall") || lowerPrompt.contains("venue") -> """
                ✨ **WedZen AI Celestial Royal Theme** ✨
                
                Recommended: **The Marigold Palace & Whispering Lights**
                *   **Palette**: Deep Shahi Crimson, Heritage Warm Gold, and pure Jasmine White.
                *   **Entrance Decor**: Magnificent floral arches created with thousands of fresh marigolds, fairy-lights, and hanging brass urlis with floating candles.
                *   **Mandap Setup**: Circular open-air mandap framed in deep-red draping, heritage brass pillars, and pure white rose garlands cascading down in symmetry.
                *   **Aesthetic Details**: Brass oil lamps, plush velvet bolsters for seating, and customizable wooden card indicators for guests.
                
                💡 *Cost Saving Tip*: Sourcing local in-season regional flowers instead of importing orchids lowers setup costs by roughly ₹60,000!
            """.trimIndent()

            lowerPrompt.contains("outfit") || lowerPrompt.contains("dress") || lowerPrompt.contains("jewelry") -> """
                👑 **WedZen AI Bridal & Groom Lookbook** 👑
                
                Embody timeless luxury and emotional harmony:
                *   **For the Bride**: A traditional handcrafted raw silk lehenga in deep crimson red, rich with heritage zardozi threadwork. Complete with double drapes (a sheer tissue gold dupatta over the head) and Kundan heritage jewelry with green emerald accents.
                *   **For the Groom**: A luxurious hand-embroidered off-white sherwani, styled with a maroon Banarasi stole, custom gold-crest safa (turban), and layered pearl groom necklaces.
                
                💡 *Regional Note*: For an Assamese style wedding, choose an elegant cream-and-gold **Muga Silk Mekhela Sador** paired with traditional Junbiri and Lokaparo neckwear.
            """.trimIndent()

            lowerPrompt.contains("expense") || lowerPrompt.contains("budget") || lowerPrompt.contains("cheaper") || lowerPrompt.contains("cost") || lowerPrompt.contains("save") -> """
                💰 **WedZen AI Budget Smart Optimizer** 💰
                
                Let's optimize your budget for the maximum royal emotional touch:
                *   **Digital Over Paper**: Choose elegant WedZen e-invitations with RSVP tracker over physical card couriers. *Immediate savings: approx. ₹15,000 - ₹30,000.*
                *   **Weekday or Off-Season Discount**: Host on a Thursday or choose a beautiful November muhurtham rather than peak January dates. *Reduction: venue rental drops by 20%.*
                *   **Combine videographer & photographer package**: Booking a single unified lens squad instead of separate boutique teams *saves ₹40,000 on average*.
                *   **Floral Hybrid Setup**: Use premium silk flowers for massive high-hanging arches and reserve fragrant fresh jasmines/marigolds for close-contact tables. *Saves around 35% on decor expenditure.*
            """.trimIndent()

            else -> """
                🌟 **WedZen Royal Wedding AI Recommendations** 🌟
                
                *   **Active Guidance**: Keep wedding details up to date under the 'Planning' panel to fine-tune estimations.
                *   **Top 3 Planning Action Steps**: 
                    1. Approve your Live Catering and Sweet-station menu.
                    2. Check the dynamic Gold price tracker on the WedZen dashboard.
                    3. Invite family members using the collaboration link to finalize vendor approval fast.
                
                *Ask me something specific! Try typing: 'Suggest menu for Punjabi styling' or 'Recommend a budget-saving theme'.*
            """.trimIndent()
        }
    }
}
