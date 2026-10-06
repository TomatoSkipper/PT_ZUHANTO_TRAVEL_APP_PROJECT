const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { GoogleGenerativeAI } = require('@google/generative-ai');

const GEMINI_API_KEY = process.env.GEMINI_API_KEY;
const genAI = new GoogleGenerativeAI(GEMINI_API_KEY);

const app = express();
app.use(bodyParser.json());
app.use(cors());

// Helper function with automatic retry for 503 Service Unavailable / high demand errors
async function generateContentWithRetry(model, prompt, maxRetries = 3, delayMs = 2000) {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
        try {
            console.log(`[Gemini AI] Attempt ${attempt} of ${maxRetries}...`);
            const result = await model.generateContent(prompt);
            return result;
        } catch (error) {
            console.warn(`[Gemini AI] Attempt ${attempt} failed: ${error.message}`);
            if (attempt === maxRetries) {
                throw error;
            }
            await new Promise(resolve => setTimeout(resolve, delayMs * attempt));
        }
    }
}

app.post('/api/chat-suggest', async (req, res) => {
    const { userMessage, destination, days } = req.body;
    console.log(`[Gemini AI] Destination: "${destination}" | Query: "${userMessage}"`);

    try {
        const model = genAI.getGenerativeModel({
            model: 'gemini-3.5-flash',
            generationConfig: { responseMimeType: 'application/json' }
        });

        const prompt = `
            You are an expert AI Travel Assistant for a travel agency.

            CONTEXT:
            - User Query: "${userMessage}"
            - Destination: "${destination}"
            - Duration: ${days} days

            TASK:
            Analyze the user's request.
            1. If the user wants a travel itinerary, schedule, or specific place recommendations, provide structured data.
            2. If the user asks a general question (e.g., weather, history, tips, chit-chat), provide a helpful conversational text answer and leave the list empty.

            JSON OUTPUT RULES:
            1. Output valid JSON strictly matching the schema below. Do not include markdown code blocks.
            2. Language MUST match the language used in "${userMessage}".
            3. Use standard 24-hour time format (HH.MM).
            4. Pricing ("estimatedPrice") MUST use the official local currency of IDR.

            JSON SCHEMA STRUCTURE:
            {
              "responseType": "itinerary" | "recommendation" | "general",
              "message": "If 'general', put the full conversational answer here (e.g., weather prediction). If 'itinerary' or 'recommendation', put a brief welcoming intro here.",
              "suggestionsList": [
                {
                  "title": "Name of Place, Experience, or Day Title",
                  "description": "Detailed description highlighting key features.",
                  "dayNumber": 1,
                  "estimatedPrice": "Estimated price with local currency code",
                  "category": "Visit | Accommodation | Dining | Activity | Itinerary",
                  "imageUrl": "https://images.unsplash.com/photo-...",
                  "timeline": [
                    {
                      "time": "09.00",
                      "activity": "Chronological activity description"
                    }
                  ]
                }
              ]
            }

            CONDITIONAL LOGIC:
            - If "responseType" is "general": Populate ONLY the "message" field with the full answer to the user's question. Output an empty array [] for "suggestionsList".
            - If "responseType" is "itinerary": Create one item per day in "suggestionsList", set "dayNumber" sequentially, and include a "timeline".
            - If "responseType" is "recommendation": Provide individual places in "suggestionsList", set "dayNumber" to null, and "timeline" to [].
            `;

        const result = await generateContentWithRetry(model, prompt);
        const responseText = result.response.text();
        const jsonResponse = JSON.parse(responseText);
        res.json(jsonResponse);

    } catch (error) {
        console.error("[Gemini AI Error after retries]:", error.message);

        // Fallback response with structured cards if Gemini JSON parsing fails
        res.json({
            message: `Here are top recommendations for ${destination}:`,
            suggestionsList: [
                {
                    title: `${destination} Highlight Spot`,
                    description: `Explore the top cultural sights and scenic spots in ${destination}.`,
                    dayNumber: 1,
                    estimatedPrice: "IDR 50,000",
                    category: "Visit",
                    imageUrl: ""
                }
            ]
        });
    }
});

const PORT = 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`==================================================`);
    console.log(`🚀 Gemini AI Travel Server running on port ${PORT} (Structured Card Mode)`);
    console.log(`==================================================`);
});

