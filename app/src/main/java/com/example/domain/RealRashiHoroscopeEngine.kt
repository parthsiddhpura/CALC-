package com.example.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

data class RashiInfo(
    val id: Int,
    val sanskritName: String,
    val hindiName: String,
    val englishName: String,
    val symbol: String,
    val rulingPlanet: String,
    val rulingPlanetHindi: String,
    val element: String, // Fire, Earth, Air, Water
    val elementHindi: String,
    val quality: String, // Chara (Cardinal), Sthira (Fixed), Dvisvabhava (Dual)
    val luckyNumbers: List<Int>,
    val luckyDay: String,
    val luckyColor: String,
    val luckyGemstone: String,
    val luckyGemstoneHindi: String,
    val friendlyRashis: List<String>,
    val nature: String,
    val positiveTraits: List<String>,
    val mantra: String,
    val personalityOverview: String,
    val careerAdvice: String,
    val healthAdvice: String,
    val wealthGuidance: String,
    val nameLetters: String
)

data class ComprehensiveAstrologyResult(
    val vedicMoonRashi: RashiInfo,       // AstroSage Primary Chandra Rashi (Vedic Moon Sign)
    val vedicSuryaRashi: RashiInfo,      // Vedic Surya Rashi (Nirayana Sun Sign)
    val nameRashi: RashiInfo?,           // AstroSage Naam Rashi (Based on Name initial)
    val nakshatraName: String,           // Vedic Nakshatra
    val nakshatraPada: Int,              // Nakshatra Charan (1, 2, 3, 4)
    val nakshatraLord: String,           // Nakshatra Lord Graha
    val moonLongitudeDeg: Double,        // Exact Sidereal Moon Longitude (0-360)
    val rashiDegreeStr: String,          // e.g. "14° 32' in Mesha"
    val westernSign: String,             // Western Tropical Zodiac
    val westernSymbol: String,
    val chineseZodiac: String,           // Chinese Lunar Year Animal
    val chineseElement: String,
    val birthStone: String,
    val dailyHoroscopeReading: String,   // Daily AstroSage Vedic Guidance
    val dailyLuckyNumber: Int,
    val dailyLuckyColor: String,
    val planetaryTransitNote: String
)

object RealRashiHoroscopeEngine {

    val RASHIS = listOf(
        RashiInfo(
            id = 1,
            sanskritName = "Mesha",
            hindiName = "मेष",
            englishName = "Aries",
            symbol = "♈",
            rulingPlanet = "Mars (Mangal)",
            rulingPlanetHindi = "मंगल देव",
            element = "Fire (Agni)",
            elementHindi = "अग्नि तत्व",
            quality = "Chara (Movable)",
            luckyNumbers = listOf(9, 1, 6),
            luckyDay = "Tuesday (मंगलवार)",
            luckyColor = "Crimson Red, Saffron, Coral",
            luckyGemstone = "Red Coral (Moonga)",
            luckyGemstoneHindi = "मूंगा (Moonga)",
            friendlyRashis = listOf("Simha", "Dhanu", "Mithuna"),
            nature = "Dynamic, courageous, assertive, bold pioneer.",
            positiveTraits = listOf("Brave", "Energetic", "Independent", "Leadership", "Passionate"),
            mantra = "ॐ क्रां क्रीं क्रौं सः भौमाय नमः (Om Kram Kreem Kroum Sah Bhaumaya Namah)",
            personalityOverview = "AstroSage Analysis: Mesha Rashi natives possess unstoppable courage and raw initiative. Mars provides natural authority, competitive spirit, and visionary executive power.",
            careerAdvice = "Excel in leadership, defense, engineering, technology architecture, surgery, sports, governance, and entrepreneurship.",
            healthAdvice = "Maintain hydration and balance fire energy with calming meditation, yoga, and cooling foods.",
            wealthGuidance = "Bold investments yield good dividends when tempered with long-term disciplined planning.",
            nameLetters = "A, L, E, I, O (अ, आ, ल, ली, लू, ले, लो, चो, चू, चे)"
        ),
        RashiInfo(
            id = 2,
            sanskritName = "Vrishabha",
            hindiName = "वृषभ",
            englishName = "Taurus",
            symbol = "♉",
            rulingPlanet = "Venus (Shukra)",
            rulingPlanetHindi = "शुक्र देव",
            element = "Earth (Prithvi)",
            elementHindi = "पृथ्वी तत्व",
            quality = "Sthira (Fixed)",
            luckyNumbers = listOf(6, 5, 8),
            luckyDay = "Friday (शुक्रवार)",
            luckyColor = "Pure White, Pastel Pink, Silver, Light Blue",
            luckyGemstone = "Diamond / White Sapphire (Heera / Opal)",
            luckyGemstoneHindi = "हीरा (Heera) / ओपल",
            friendlyRashis = listOf("Kanya", "Makara", "Tula"),
            nature = "Reliable, patient, grounded, artistic and tenacious.",
            positiveTraits = listOf("Steadfast", "Loyal", "Aesthetic sense", "Pragmatic", "Determined"),
            mantra = "ॐ द्रां द्रीं द्रौं सः शुक्राय नमः (Om Dram Dreem Droum Sah Shukraya Namah)",
            personalityOverview = "AstroSage Analysis: Vrishabha natives are gifted with artistic finesse, financial acumen, resilience, and grounded steadfastness under Venusian blessings.",
            careerAdvice = "Outstanding success in banking, finance, hospitality, interior design, fine arts, luxury goods, and real estate.",
            healthAdvice = "Care for vocal cords and throat; engage in regular walking or yoga for physical vitality.",
            wealthGuidance = "Master asset accumulator; favors tangible wealth, land, and steady blue-chip investments.",
            nameLetters = "B, V, U, W (इ, ई, उ, ऊ, ए, ऐ, ओ, बा, बी, बू, बे, बो, वा, वी, वू, वे, वो)"
        ),
        RashiInfo(
            id = 3,
            sanskritName = "Mithuna",
            hindiName = "मिथुन",
            englishName = "Gemini",
            symbol = "♊",
            rulingPlanet = "Mercury (Budha)",
            rulingPlanetHindi = "बुध देव",
            element = "Air (Vayu)",
            elementHindi = "वायु तत्व",
            quality = "Dvisvabhava (Dual)",
            luckyNumbers = listOf(5, 3, 7),
            luckyDay = "Wednesday (बुधवार)",
            luckyColor = "Emerald Green, Light Green, Cyan",
            luckyGemstone = "Emerald (Panna)",
            luckyGemstoneHindi = "पन्ना (Panna)",
            friendlyRashis = listOf("Tula", "Kumbha", "Mesha"),
            nature = "Intellectual, witty, versatile, expressive and curious.",
            positiveTraits = listOf("Articulate", "Adaptable", "Fast Learner", "Charming", "Analytical"),
            mantra = "ॐ ब्रां ब्रीं ब्रौं सः बुधाय नमः (Om Bram Breem Broum Sah Budhaya Namah)",
            personalityOverview = "AstroSage Analysis: Mithuna is the communicator of the cosmos. Budha grants sharp intellect, multilingual versatility, analytical aptitude, and quick negotiation power.",
            careerAdvice = "Flourish in journalism, software engineering, public relations, trading, marketing, analytics, and writing.",
            healthAdvice = "Support nervous system health with regular sleep cycles and mindful deep breathing.",
            wealthGuidance = "Multiple revenue streams and intellectual property create exceptional financial prosperity.",
            nameLetters = "K, CHH, GH, Q, C (का, की, कू, घ, ङ, छ, के, को, ह, हा, ही)"
        ),
        RashiInfo(
            id = 4,
            sanskritName = "Karka",
            hindiName = "कर्क",
            englishName = "Cancer",
            symbol = "♋",
            rulingPlanet = "Moon (Chandra)",
            rulingPlanetHindi = "चंद्र देव",
            element = "Water (Jala)",
            elementHindi = "जल तत्व",
            quality = "Chara (Movable)",
            luckyNumbers = listOf(2, 7, 9),
            luckyDay = "Monday (सोमवार)",
            luckyColor = "Pearl White, Silver, Cream, Sea Green",
            luckyGemstone = "Natural Pearl (Moti) / Moonstone",
            luckyGemstoneHindi = "मोती (Moti)",
            friendlyRashis = listOf("Vrishchika", "Meena", "Vrishabha"),
            nature = "Intuitive, nurturing, empathetic, highly perceptive and devoted.",
            positiveTraits = listOf("Empathetic", "Protective", "Imaginative", "Loyal", "Intuitive"),
            mantra = "ॐ श्रां श्रीं श्रौं सः चंद्रमसे नमः (Om Shram Shreem Shroum Sah Chandramase Namah)",
            personalityOverview = "AstroSage Analysis: Karka Rashi is deeply attuned to emotional intelligence, intuition, and memory. Chandra bestows immense empathy, creativity, and steadfast loyalty.",
            careerAdvice = "Great fulfillment in healthcare, teaching, maritime, psychology, architecture, administration, and public welfare.",
            healthAdvice = "Protect digestion and emotional wellbeing; stay close to peaceful natural bodies of water.",
            wealthGuidance = "Cautious and protective with finances; builds enduring multigenerational family security.",
            nameLetters = "H, D, DD (ही, हू, हे, हो, डा, डी, डू, डे, डो, दा, दी, दू, दे, दो)"
        ),
        RashiInfo(
            id = 5,
            sanskritName = "Simha",
            hindiName = "सिंह",
            englishName = "Leo",
            symbol = "♌",
            rulingPlanet = "Sun (Surya)",
            rulingPlanetHindi = "सूर्य देव",
            element = "Fire (Agni)",
            elementHindi = "अग्नि तत्व",
            quality = "Sthira (Fixed)",
            luckyNumbers = listOf(1, 4, 9),
            luckyDay = "Sunday (रविवार)",
            luckyColor = "Golden Yellow, Royal Orange, Ruby, Saffron",
            luckyGemstone = "Ruby (Manikya)",
            luckyGemstoneHindi = "माणिक्य (Manikya)",
            friendlyRashis = listOf("Mesha", "Dhanu", "Tula"),
            nature = "Regal, charismatic, magnanimous, confident and noble-hearted.",
            positiveTraits = listOf("Leader", "Warm-hearted", "Generous", "Honorable", "Inspiring"),
            mantra = "ॐ ह्रां ह्रीं ह्रौं सः सूर्याय नमः (Om Hram Hreem Hroum Sah Suryaya Namah)",
            personalityOverview = "AstroSage Analysis: Simha embodies the sovereign vitality of Surya Dev. You radiate natural leadership, dignity, courage, and warmth that uplifts your peers.",
            careerAdvice = "Top executive leadership, governance, civil services, entertainment, corporate management, and entrepreneurship.",
            healthAdvice = "Maintain cardiovascular health with regular aerobic activity and morning sun exposure (Surya Namaskar).",
            wealthGuidance = "Prosperity comes through grand visions and authoritative positions; practice balanced budgeting.",
            nameLetters = "M, T, TT (मा, मी, मू, मे, मो, टा, टी, टू, टे)"
        ),
        RashiInfo(
            id = 6,
            sanskritName = "Kanya",
            hindiName = "कन्या",
            englishName = "Virgo",
            symbol = "♍",
            rulingPlanet = "Mercury (Budha)",
            rulingPlanetHindi = "बुध देव",
            element = "Earth (Prithvi)",
            elementHindi = "पृथ्वी तत्व",
            quality = "Dvisvabhava (Dual)",
            luckyNumbers = listOf(5, 2, 7),
            luckyDay = "Wednesday (बुधवार)",
            luckyColor = "Dark Green, Navy Blue, Grey, Emerald",
            luckyGemstone = "Emerald (Panna)",
            luckyGemstoneHindi = "पन्ना (Panna)",
            friendlyRashis = listOf("Vrishabha", "Makara", "Mithuna"),
            nature = "Analytical, precise, service-oriented, discerning and methodical.",
            positiveTraits = listOf("Detail-oriented", "Helpful", "Intelligent", "Disciplined", "Practical"),
            mantra = "ॐ बुं बुधाय नमः (Om Bum Budhaya Namah)",
            personalityOverview = "AstroSage Analysis: Kanya natives possess unmatched analytical precision, dedication to perfection, and an earnest desire to optimize processes and heal surroundings.",
            careerAdvice = "Remarkable success in software architecture, data science, accounting, medicine, auditing, writing, and research.",
            healthAdvice = "Maintain a clean, wholesome diet with fiber-rich foods; practice stress management to calm an active mind.",
            wealthGuidance = "Master of budgeting and smart asset management; avoids risky speculation in favor of solid returns.",
            nameLetters = "P, TTH, SH (टो, पा, पी, पू, ष, ण, ठ, पे, पो)"
        ),
        RashiInfo(
            id = 7,
            sanskritName = "Tula",
            hindiName = "तुला",
            englishName = "Libra",
            symbol = "♎",
            rulingPlanet = "Venus (Shukra)",
            rulingPlanetHindi = "शुक्र देव",
            element = "Air (Vayu)",
            elementHindi = "वायु तत्व",
            quality = "Chara (Movable)",
            luckyNumbers = listOf(6, 9, 15),
            luckyDay = "Friday (शुक्रवार)",
            luckyColor = "Royal Blue, Opal White, Jade Green, Silver",
            luckyGemstone = "Diamond / White Zircon (Heera / Jarkan)",
            luckyGemstoneHindi = "हीरा (Heera) / जरकन",
            friendlyRashis = listOf("Mithuna", "Kumbha", "Simha"),
            nature = "Diplomatic, harmonious, refined, socially gifted and justice-loving.",
            positiveTraits = listOf("Balanced", "Charming", "Fair-minded", "Diplomatic", "Artistic"),
            mantra = "ॐ शुं शुक्राय नमः (Om Shum Shukraya Namah)",
            personalityOverview = "AstroSage Analysis: Tula signifies universal equilibrium and aesthetics. Shukra endows innate elegance, negotiation finesse, and an enduring quest for fairness.",
            careerAdvice = "Excel in law, diplomacy, fashion, architecture, mediation, industrial design, partnerships, and consultancy.",
            healthAdvice = "Keep kidney and lower back health optimal by drinking plenty of water and stretching daily.",
            wealthGuidance = "Gains wealth through strategic collaborations, aesthetic ventures, and win-win negotiations.",
            nameLetters = "R, T, TT (रा, री, रू, रे, रो, ता, ती, तू, ते)"
        ),
        RashiInfo(
            id = 8,
            sanskritName = "Vrishchika",
            hindiName = "वृश्चिक",
            englishName = "Scorpio",
            symbol = "♏",
            rulingPlanet = "Mars / Ketu (Mangal)",
            rulingPlanetHindi = "मंगल व केतु",
            element = "Water (Jala)",
            elementHindi = "जल तत्व",
            quality = "Sthira (Fixed)",
            luckyNumbers = listOf(9, 4, 8),
            luckyDay = "Tuesday (मंगलवार)",
            luckyColor = "Deep Maroon, Rust, Crimson, Jet Black",
            luckyGemstone = "Red Coral (Moonga)",
            luckyGemstoneHindi = "मूंगा (Moonga)",
            friendlyRashis = listOf("Karka", "Meena", "Vrishchika"),
            nature = "Intense, deeply perceptive, resilient, transformative and loyal.",
            positiveTraits = listOf("Passionate", "Strategic", "Intuitive", "Resilient", "Magnetic"),
            mantra = "ॐ अं अंगारकाय नमः (Om Am Angarakaya Namah)",
            personalityOverview = "AstroSage Analysis: Vrishchika natives possess profound emotional depth, piercing intellect, and invincible willpower to rise like the phoenix through any adversity.",
            careerAdvice = "Distinction in investigative research, surgery, cybersecurity, deep tech, forensic finance, and crisis management.",
            healthAdvice = "Practice emotional release through martial arts, swimming, or vigorous physical exercise.",
            wealthGuidance = "Extraordinary knack for unlocking hidden value, turning around distressed assets, and compounding growth.",
            nameLetters = "N, Y (तो, ना, नी, नू, ने, नो, या, यी, यू)"
        ),
        RashiInfo(
            id = 9,
            sanskritName = "Dhanu",
            hindiName = "धनु",
            englishName = "Sagittarius",
            symbol = "♐",
            rulingPlanet = "Jupiter (Guru / Brihaspati)",
            rulingPlanetHindi = "बृहस्पति देव",
            element = "Fire (Agni)",
            elementHindi = "अग्नि तत्व",
            quality = "Dvisvabhava (Dual)",
            luckyNumbers = listOf(3, 9, 12),
            luckyDay = "Thursday (गुरुवार)",
            luckyColor = "Saffron, Golden Yellow, Purple",
            luckyGemstone = "Yellow Sapphire (Pukhraj)",
            luckyGemstoneHindi = "पुखराज (Pukhraj)",
            friendlyRashis = listOf("Mesha", "Simha", "Meena"),
            nature = "Philosophical, optimistic, truth-seeking, expansive and visionary.",
            positiveTraits = listOf("Visionary", "Generous", "Truth-lover", "Enthusiastic", "Wise"),
            mantra = "ॐ ग्रां ग्रीं ग्रौं सः गुरवे नमः (Om Gram Greem Groum Sah Guruve Namah)",
            personalityOverview = "AstroSage Analysis: Dhanu is the archer of wisdom and noble purpose. Brihaspati bestows unquenchable thirst for higher knowledge, optimism, exploration, and dharma.",
            careerAdvice = "Thrive in higher education, academia, global trade, philosophy, publishing, aviation, law, and spiritual leadership.",
            healthAdvice = "Guard liver and hip vitality; enjoy outdoor trekking, sports, and mindful nutrition.",
            wealthGuidance = "Blessed with divine fortune when investments align with ethical principles and broad horizons.",
            nameLetters = "BH, DH, F, PH, Y (ये, यो, भा, भी, भू, धा, फा, ढा, भे)"
        ),
        RashiInfo(
            id = 10,
            sanskritName = "Makara",
            hindiName = "मकर",
            englishName = "Capricorn",
            symbol = "♑",
            rulingPlanet = "Saturn (Shani)",
            rulingPlanetHindi = "शनि देव",
            element = "Earth (Prithvi)",
            elementHindi = "पृथ्वी तत्व",
            quality = "Chara (Movable)",
            luckyNumbers = listOf(8, 6, 4),
            luckyDay = "Saturday (शनिवार)",
            luckyColor = "Charcoal, Navy Blue, Steel Grey, Black",
            luckyGemstone = "Blue Sapphire (Neelam)",
            luckyGemstoneHindi = "नीलम (Neelam)",
            friendlyRashis = listOf("Vrishabha", "Kanya", "Kumbha"),
            nature = "Disciplined, ambitious, patient, strategic and steadfastly determined.",
            positiveTraits = listOf("Persistent", "Organized", "Pragmatic", "Responsible", "Enduring"),
            mantra = "ॐ प्रां प्रीं प्रौं सः शनैश्चराय नमः (Om Pram Preem Proum Sah Shanaishcharaya Namah)",
            personalityOverview = "AstroSage Analysis: Makara natives climb every mountain through patience, rigorous discipline, and long-term strategic execution under Shani Dev's guidance.",
            careerAdvice = "High ranks in corporate hierarchy, civil services, construction, industrial manufacturing, governance, and long-term ventures.",
            healthAdvice = "Care for knees, joints, and skeletal strength with calcium intake and consistent mobility workouts.",
            wealthGuidance = "Tremendous long-term wealth compounder; gains massive prosperity through disciplined patience.",
            nameLetters = "KH, J, G (भो, जा, जी, खी, खू, खे, खो, गा, गी)"
        ),
        RashiInfo(
            id = 11,
            sanskritName = "Kumbha",
            hindiName = "कुंभ",
            englishName = "Aquarius",
            symbol = "♒",
            rulingPlanet = "Saturn (Shani)",
            rulingPlanetHindi = "शनि देव",
            element = "Air (Vayu)",
            elementHindi = "वायु तत्व",
            quality = "Sthira (Fixed)",
            luckyNumbers = listOf(8, 4, 7),
            luckyDay = "Saturday (शनिवार)",
            luckyColor = "Electric Blue, Turquoise, Aquamarine, Violet",
            luckyGemstone = "Blue Sapphire / Hessonite (Neelam / Gomed)",
            luckyGemstoneHindi = "नीलम (Neelam) / गोमेद",
            friendlyRashis = listOf("Mithuna", "Tula", "Makara"),
            nature = "Visionary, humanitarian, progressive, inventive and independent.",
            positiveTraits = listOf("Innovative", "Humanitarian", "Forward-thinking", "Objective", "Inventive"),
            mantra = "ॐ शं शनैश्चराय नमः (Om Sham Shanaishcharaya Namah)",
            personalityOverview = "AstroSage Analysis: Kumbha represents the water-bearer of universal knowledge. You are an egalitarian visionary dedicated to scientific progress and human upliftment.",
            careerAdvice = "Pioneering work in AI, aerospace, renewable energy, social revolution, scientific research, and global networks.",
            healthAdvice = "Support circulatory system and calves; engage in regular stretching and deep rest.",
            wealthGuidance = "Prosperity emerges from revolutionary technologies, decentralized networks, and forward-looking concepts.",
            nameLetters = "G, S, SH (गू, गे, गो, सा, सी, सू, से, सो, दा)"
        ),
        RashiInfo(
            id = 12,
            sanskritName = "Meena",
            hindiName = "मीन",
            englishName = "Pisces",
            symbol = "♓",
            rulingPlanet = "Jupiter (Guru)",
            rulingPlanetHindi = "बृहस्पति देव",
            element = "Water (Jala)",
            elementHindi = "जल तत्व",
            quality = "Dvisvabhava (Dual)",
            luckyNumbers = listOf(3, 7, 9),
            luckyDay = "Thursday (गुरुवार)",
            luckyColor = "Sea Green, Lavender, Golden Yellow, Aqua",
            luckyGemstone = "Yellow Sapphire (Pukhraj)",
            luckyGemstoneHindi = "पुखराज (Pukhraj)",
            friendlyRashis = listOf("Karka", "Vrishchika", "Dhanu"),
            nature = "Compassionate, artistic, intuitive, spiritual and benevolent.",
            positiveTraits = listOf("Kind", "Mystical", "Imaginative", "Generous", "Sensitive"),
            mantra = "ॐ बृं बृहस्पतये नमः (Om Brim Brihaspataye Namah)",
            personalityOverview = "AstroSage Analysis: Meena natives embody divine empathy, mystical intuition, and boundless creative imagination connecting human spirit with infinity.",
            careerAdvice = "Great heights in cinema, poetry, pharmaceuticals, oceanography, spiritual teaching, music, healing, and philanthropic trusts.",
            healthAdvice = "Guard feet and lymphatic system; prioritize quality sleep, meditation, and creative outlets.",
            wealthGuidance = "Attracts abundance effortlessly when engaged in meaningful work that benefits the world.",
            nameLetters = "D, CH, Z, TH, J (दी, दू, थ, झ, ञ, दे, दो, चा, ची)"
        )
    )

    private val NAKSHATRAS = listOf(
        "Ashwini (अश्विनी)" to "Ketu (केतु)",
        "Bharani (भरणी)" to "Venus (शुक्र)",
        "Krittika (कृत्तिका)" to "Sun (सूर्य)",
        "Rohini (रोहिणी)" to "Moon (चंद्र)",
        "Mrigashira (मृगशिरा)" to "Mars (मंगल)",
        "Ardra (आर्द्रा)" to "Rahu (राहु)",
        "Punarvasu (पुनर्वसु)" to "Jupiter (गुरु)",
        "Pushya (पुष्य)" to "Saturn (शनि)",
        "Ashlesha (अश्लेषा)" to "Mercury (बुध)",
        "Magha (मघा)" to "Ketu (केतु)",
        "Purva Phalguni (पूर्वा फाल्गुनी)" to "Venus (शुक्र)",
        "Uttara Phalguni (उत्तरा फाल्गुनी)" to "Sun (सूर्य)",
        "Hasta (हस्त)" to "Moon (चंद्र)",
        "Chitra (चित्रा)" to "Mars (मंगल)",
        "Swati (स्वाति)" to "Rahu (राहु)",
        "Vishakha (विशाखा)" to "Jupiter (गुरु)",
        "Anuradha (अनुराधा)" to "Saturn (शनि)",
        "Jyeshtha (ज्येष्ठा)" to "Mercury (बुध)",
        "Mula (मूल)" to "Ketu (केतु)",
        "Purva Ashadha (पूर्वाषाढ़ा)" to "Venus (शुक्र)",
        "Uttara Ashadha (उत्तराषाढ़ा)" to "Sun (सूर्य)",
        "Shravana (श्रवण)" to "Moon (चंद्र)",
        "Dhanishta (धनिष्ठा)" to "Mars (मंगल)",
        "Shatabhisha (शतभिषा)" to "Rahu (राहु)",
        "Purva Bhadrapada (पूर्वभाद्रपदा)" to "Jupiter (गुरु)",
        "Uttara Bhadrapada (उत्तरभाद्रपदा)" to "Saturn (शनि)",
        "Revati (रेवती)" to "Mercury (बुध)"
    )

    /**
     * Finds AstroSage Naam Rashi from person's name initial letter.
     */
    fun findNaamRashi(name: String): RashiInfo? {
        if (name.isBlank()) return null
        val cleanName = name.trim().uppercase(Locale.ENGLISH)
        return when {
            cleanName.startsWith("CH") || cleanName.startsWith("C") -> RASHIS[2] // Mithuna / Meena
            cleanName.startsWith("A") || cleanName.startsWith("L") || cleanName.startsWith("E") || cleanName.startsWith("I") || cleanName.startsWith("O") -> RASHIS[0] // Mesha
            cleanName.startsWith("B") || cleanName.startsWith("V") || cleanName.startsWith("U") || cleanName.startsWith("W") -> RASHIS[1] // Vrishabha
            cleanName.startsWith("K") || cleanName.startsWith("GH") || cleanName.startsWith("Q") -> RASHIS[2] // Mithuna
            cleanName.startsWith("H") || cleanName.startsWith("D") -> RASHIS[3] // Karka
            cleanName.startsWith("M") || cleanName.startsWith("T") -> RASHIS[4] // Simha
            cleanName.startsWith("P") || cleanName.startsWith("SH") -> RASHIS[5] // Kanya
            cleanName.startsWith("R") -> RASHIS[6] // Tula
            cleanName.startsWith("N") || cleanName.startsWith("Y") -> RASHIS[7] // Vrishchika
            cleanName.startsWith("BH") || cleanName.startsWith("DH") || cleanName.startsWith("F") || cleanName.startsWith("PH") -> RASHIS[8] // Dhanu
            cleanName.startsWith("KH") || cleanName.startsWith("J") -> RASHIS[9] // Makara
            cleanName.startsWith("G") || cleanName.startsWith("S") -> RASHIS[10] // Kumbha
            cleanName.startsWith("Z") || cleanName.startsWith("TH") -> RASHIS[11] // Meena
            else -> null
        }
    }

    /**
     * Real Vedic Moon calculation (Chandra Sthiti) based on AstroSage / Lahiri Ayanamsha (Chitra Paksha).
     */
    fun calculateAstrology(
        date: LocalDate,
        hour: Int = 12,
        minute: Int = 0,
        personName: String = ""
    ): ComprehensiveAstrologyResult {
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth

        // 1. Precise Julian Day Calculation
        val a = floor((14.0 - month) / 12.0)
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        val jd = day + floor((153 * m + 2) / 5.0) + 365 * y + floor(y / 4.0) - floor(y / 100.0) + floor(y / 400.0) - 32045.0 + (hour + minute / 60.0) / 24.0

        // Days since J2000.0 (JD 2451545.0)
        val d = jd - 2451545.0
        val t = d / 36525.0

        // 2. Astronomical Mean Elements of the Moon (Meeus Ephemeris)
        var l0 = 218.3164477 + 481267.88128 * t
        var mSun = 357.5291092 + 35999.05029 * t
        var mMoon = 134.9633964 + 477198.867505 * t
        var elongationD = 297.8501921 + 445267.111403 * t
        var f = 93.2720950 + 483202.017523 * t

        // Normalize degrees to 0..360
        fun norm(deg: Double): Double {
            var res = deg % 360.0
            if (res < 0) res += 360.0
            return res
        }

        fun rad(deg: Double) = Math.toRadians(deg)

        l0 = norm(l0)
        mSun = norm(mSun)
        mMoon = norm(mMoon)
        elongationD = norm(elongationD)
        f = norm(f)

        // 3. Principal Lunar Perturbations for Geocentric Ecliptic Longitude
        val dLambda = 6.288774 * sin(rad(mMoon)) +
                1.274027 * sin(rad(2 * elongationD - mMoon)) +
                0.658309 * sin(rad(2 * elongationD)) +
                0.213618 * sin(rad(2 * mMoon)) -
                0.185116 * sin(rad(mSun)) -
                0.114332 * sin(rad(2 * f)) +
                0.058793 * sin(rad(2 * elongationD - 2 * mMoon)) +
                0.057066 * sin(rad(2 * elongationD - mSun - mMoon)) +
                0.053322 * sin(rad(2 * elongationD + mMoon)) +
                0.046058 * sin(rad(2 * elongationD - mSun))

        val tropicalMoonLong = norm(l0 + dLambda)

        // 4. AstroSage Standard Lahiri (Chitra Paksha) Ayanamsha
        val ayanamsha = 23.85 + (year - 2000 + (month - 1) / 12.0 + day / 365.25) * 0.01397
        val siderealMoonLong = norm(tropicalMoonLong - ayanamsha)

        // 5. AstroSage Moon Sign (Chandra Rashi)
        val moonRashiIndex = (floor(siderealMoonLong / 30.0).toInt()).coerceIn(0, 11)
        val vedicMoonRashi = RASHIS[moonRashiIndex]

        val degInSign = siderealMoonLong - (moonRashiIndex * 30.0)
        val degWhole = degInSign.toInt()
        val minWhole = ((degInSign - degWhole) * 60).toInt()
        val rashiDegreeStr = "$degWhole° ${String.format("%02d", minWhole)}' in ${vedicMoonRashi.sanskritName}"

        // 6. AstroSage Nakshatra & Pada
        val nakshatraSpan = 360.0 / 27.0 // 13.333333 degrees
        val nakshatraIndex = (floor(siderealMoonLong / nakshatraSpan).toInt()).coerceIn(0, 26)
        val (nakshatraName, nakshatraLord) = NAKSHATRAS[nakshatraIndex]
        val padaSpan = nakshatraSpan / 4.0 // 3.333333 degrees
        val nakshatraRemainder = siderealMoonLong % nakshatraSpan
        val nakshatraPada = (floor(nakshatraRemainder / padaSpan).toInt() + 1).coerceIn(1, 4)

        // 7. Vedic Surya Rashi (Nirayana Sun Sign based on Solar Sankranti)
        val suryaRashiIndex = when {
            (month == 4 && day >= 14) || (month == 5 && day <= 14) -> 0 // Mesha
            (month == 5 && day >= 15) || (month == 6 && day <= 14) -> 1 // Vrishabha
            (month == 6 && day >= 15) || (month == 7 && day <= 15) -> 2 // Mithuna
            (month == 7 && day >= 16) || (month == 8 && day <= 16) -> 3 // Karka
            (month == 8 && day >= 17) || (month == 9 && day <= 16) -> 4 // Simha
            (month == 9 && day >= 17) || (month == 10 && day <= 16) -> 5 // Kanya
            (month == 10 && day >= 17) || (month == 11 && day <= 15) -> 6 // Tula
            (month == 11 && day >= 16) || (month == 12 && day <= 15) -> 7 // Vrishchika
            (month == 12 && day >= 16) || (month == 1 && day <= 13) -> 8 // Dhanu
            (month == 1 && day >= 14) || (month == 2 && day <= 12) -> 9 // Makara
            (month == 2 && day >= 13) || (month == 3 && day <= 13) -> 10 // Kumbha
            else -> 11 // Meena
        }
        val vedicSuryaRashi = RASHIS[suryaRashiIndex]

        // 8. AstroSage Naam Rashi
        val nameRashi = findNaamRashi(personName)

        // 9. Western Zodiac
        val (westernSign, westernSymbol) = when (month) {
            1 -> if (day < 20) "Capricorn" to "♑" else "Aquarius" to "♒"
            2 -> if (day < 19) "Aquarius" to "♒" else "Pisces" to "♓"
            3 -> if (day < 21) "Pisces" to "♓" else "Aries" to "♈"
            4 -> if (day < 20) "Aries" to "♈" else "Taurus" to "♉"
            5 -> if (day < 21) "Taurus" to "♉" else "Gemini" to "♊"
            6 -> if (day < 21) "Gemini" to "♊" else "Cancer" to "♋"
            7 -> if (day < 23) "Cancer" to "♋" else "Leo" to "♌"
            8 -> if (day < 23) "Leo" to "♌" else "Virgo" to "♍"
            9 -> if (day < 23) "Virgo" to "♍" else "Libra" to "♎"
            10 -> if (day < 23) "Libra" to "♎" else "Scorpio" to "♏"
            11 -> if (day < 22) "Scorpio" to "♏" else "Sagittarius" to "♐"
            12 -> if (day < 22) "Sagittarius" to "♐" else "Capricorn" to "♑"
            else -> "Aries" to "♈"
        }

        // 10. Chinese Zodiac & Element
        val chineseZodiacs = listOf("Rat 🐀", "Ox 🐂", "Tiger 🐅", "Rabbit 🐇", "Dragon 🐉", "Snake 🐍", "Horse 🐎", "Goat 🐐", "Monkey 🐒", "Rooster 🐓", "Dog 🐕", "Pig 🐖")
        val chineseIndex = (year - 4) % 12
        val chineseSign = chineseZodiacs[if (chineseIndex >= 0) chineseIndex else chineseIndex + 12]

        val chineseElements = listOf("Metal", "Water", "Wood", "Fire", "Earth")
        val elemIndex = ((year % 10) / 2) % 5
        val chineseElem = chineseElements[elemIndex]

        // 11. Traditional Birthstone
        val birthstone = when (month) {
            1 -> "Garnet"
            2 -> "Amethyst"
            3 -> "Aquamarine"
            4 -> "Diamond"
            5 -> "Emerald"
            6 -> "Pearl / Alexandrite"
            7 -> "Ruby"
            8 -> "Peridot"
            9 -> "Sapphire"
            10 -> "Opal / Tourmaline"
            11 -> "Topaz / Citrine"
            12 -> "Tanzanite / Turquoise"
            else -> "Quartz"
        }

        // 12. AstroSage Daily Horoscope Reading
        val today = LocalDate.now()
        val dailySeed = (today.dayOfYear + vedicMoonRashi.id * 11 + nakshatraIndex) % 6
        val dailyReadings = listOf(
            "AstroSage Transit: The Moon aligns favorably with your natal lagna. Bold decisions in career, professional investments, and family conversations are highly auspicious today.",
            "AstroSage Transit: Jupiter's harmonious aspect stimulates creative clarity and interpersonal harmony. A valuable long-term opportunity or financial lead emerges today.",
            "AstroSage Transit: Mercury activates your analytical prowess and communications. A superb day for contracts, calculations, exams, and financial planning.",
            "AstroSage Transit: Auspicious lunar transit brings grounded peace and family joy. Prioritize health, wholesome nutrition, and creative undertakings.",
            "AstroSage Transit: Radiant planetary energy elevates your leadership, prestige, and confidence. Trust your refined intuition during key negotiations.",
            "AstroSage Transit: Favorable Venusian vibrations enhance partnerships, artistic endeavors, and wealth gains. A joyful conversation awaits you."
        )
        val dailyReading = dailyReadings[dailySeed]

        val dailyLuckyNumber = (vedicMoonRashi.luckyNumbers.first() * (today.dayOfMonth % 3 + 1)) % 9 + 1
        val dailyLuckyColor = vedicMoonRashi.luckyColor.split(",").first().trim()
        val transitNote = "Chandra Transit in ${vedicMoonRashi.elementHindi} under Lord ${vedicMoonRashi.rulingPlanetHindi}."

        return ComprehensiveAstrologyResult(
            vedicMoonRashi = vedicMoonRashi,
            vedicSuryaRashi = vedicSuryaRashi,
            nameRashi = nameRashi,
            nakshatraName = nakshatraName,
            nakshatraPada = nakshatraPada,
            nakshatraLord = nakshatraLord,
            moonLongitudeDeg = siderealMoonLong,
            rashiDegreeStr = rashiDegreeStr,
            westernSign = westernSign,
            westernSymbol = westernSymbol,
            chineseZodiac = chineseSign,
            chineseElement = chineseElem,
            birthStone = birthstone,
            dailyHoroscopeReading = dailyReading,
            dailyLuckyNumber = dailyLuckyNumber,
            dailyLuckyColor = dailyLuckyColor,
            planetaryTransitNote = transitNote
        )
    }
}
