(ns main
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(defonce state (r/atom {:room-description nil
                        :embed-mode (boolean (re-find #"[?&]embed" (.-search js/location)))
                        :copied false}))

(defn copy-to-clipboard [text]
  (let [el (.createElement js/document "textarea")]
    (set! (.-value el) text)
    (.appendChild (.-body js/document) el)
    (.select el)
    (.execCommand js/document "copy")
    (.removeChild (.-body js/document) el)))

;; Room description data
(def room-sizes
  ["tiny" "small" "modest" "medium-sized" "large" "spacious" "vast" "enormous" "cavernous" "colossal"])

(def room-shapes
  ["square" "rectangular" "circular" "oval" "hexagonal" "octagonal" "triangular" "irregularly shaped" 
   "L-shaped" "cross-shaped" "dome-shaped" "vaulted" "narrow" "wide" "elongated"])

(def room-adjectives
  ["damp" "musty" "dark" "dimly lit" "well-lit" "shadowy" "gloomy" "eerie" "foreboding" "ancient" 
   "decrepit" "crumbling" "pristine" "ornate" "plain" "simple" "elaborate" "mysterious" "forgotten" 
   "abandoned" "haunting" "cold" "warm" "hot" "freezing" "dusty" "clean" "tidy" "messy" "cluttered" 
   "barren" "empty" "hollow" "echoing" "silent" "quiet" "noisy" "rumbling" "vibrating" "still" 
   "peaceful" "chaotic" "orderly" "symmetrical" "asymmetrical" "uneven" "smooth" "rough" "jagged" 
   "worn" "weathered" "polished" "gleaming" "shimmering" "glowing" "dark" "bright" "colorful" "dull" 
   "faded" "vibrant" "muted" "stark" "bleak" "dreary" "cheerful" "somber" "solemn" "sacred" 
   "profane" "unholy" "blessed" "cursed" "enchanted" "magical" "mundane" "ordinary" "extraordinary" 
   "remarkable" "unremarkable" "distinctive" "nondescript" "featureless" "detailed" "intricate" 
   "simple" "complex" "puzzling" "straightforward" "confusing" "disorienting" "familiar" "strange" 
   "alien" "otherworldly" "natural" "unnatural" "artificial" "constructed" "carved" "hewn" "built" 
   "grown" "formed" "shaped" "molded" "crafted" "designed" "planned" "haphazard" "random" "chaotic" 
   "ordered" "structured" "patterned" "decorated" "adorned" "embellished" "plain" "unadorned" 
   "spartan" "luxurious" "opulent" "modest" "humble" "grand" "magnificent" "impressive" "imposing" 
   "intimidating" "welcoming" "inviting" "forbidding" "threatening" "dangerous" "safe" "secure" 
   "unstable" "precarious" "solid" "sturdy" "fragile" "delicate" "robust" "weak" "strong" "powerful" 
   "feeble" "mighty" "insignificant" "important" "central" "peripheral" "isolated" "connected" 
   "linked" "separated" "divided" "unified" "whole" "fractured" "broken" "intact" "complete" 
   "partial" "unfinished" "finished" "perfect" "flawed" "damaged" "pristine" "new" "old" "ancient" 
   "recent" "timeless" "temporary" "permanent" "ephemeral" "enduring" "lasting" "fleeting" 
   "transient" "stable" "changing" "dynamic" "static" "fixed" "fluid" "rigid" "flexible" "adaptable" 
   "unyielding" "yielding" "resistant" "susceptible" "vulnerable" "protected" "exposed" "hidden" 
   "visible" "obvious" "subtle" "nuanced" "clear" "obscured" "veiled" "revealed" "concealed" 
   "disclosed" "secret" "known" "mysterious" "enigmatic" "puzzling" "straightforward" "complex" 
   "simple" "elaborate" "intricate" "detailed" "sparse" "minimal" "maximal" "excessive" "moderate" 
   "balanced" "harmonious" "discordant" "jarring" "soothing" "calming" "disturbing" "unsettling" 
   "comforting" "reassuring" "alarming" "frightening" "terrifying" "horrifying" "pleasant" 
   "unpleasant" "agreeable" "disagreeable" "appealing" "unappealing" "attractive" "repulsive" 
   "beautiful" "ugly" "pretty" "hideous" "grotesque" "elegant" "graceful" "awkward" "clumsy" 
   "refined" "crude" "sophisticated" "primitive" "advanced" "rudimentary" "basic" "complex" 
   "intricate" "simple" "plain" "fancy" "ornate" "decorated" "undecorated" "embellished" 
   "unembellished" "adorned" "unadorned" "garnished" "ungarnished" "trimmed" "untrimmed" 
   "furnished" "unfurnished" "equipped" "unequipped" "stocked" "unstocked" "filled" "unfilled" 
   "occupied" "unoccupied" "inhabited" "uninhabited" "populated" "unpopulated" "crowded" 
   "uncrowded" "busy" "quiet" "noisy" "silent" "loud" "soft" "harsh" "gentle" "rough" "smooth" 
   "textured" "untextured" "patterned" "unpatterned" "designed" "undesigned" "planned" "unplanned" 
   "organized" "unorganized" "arranged" "unarranged" "ordered" "disordered" "neat" "messy" "tidy" 
   "untidy" "clean" "dirty" "spotless" "spotted" "stained" "unstained" "marked" "unmarked" 
   "scratched" "unscratched" "dented" "undented" "damaged" "undamaged" "broken" "unbroken" 
   "shattered" "unshattered" "cracked" "uncracked" "split" "unsplit" "torn" "untorn" "ripped" 
   "unripped" "cut" "uncut" "sliced" "unsliced" "chopped" "unchopped" "hacked" "unhacked" 
   "severed" "unsevered" "disconnected" "connected" "joined" "disjoined" "attached" "detached" 
   "fixed" "unfixed" "secured" "unsecured" "fastened" "unfastened" "tied" "untied" "bound" 
   "unbound" "wrapped" "unwrapped" "covered" "uncovered" "exposed" "unexposed" "revealed" 
   "unrevealed" "disclosed" "undisclosed" "shown" "unshown" "displayed" "undisplayed" "exhibited" 
   "unexphibited" "demonstrated" "undemonstrated" "presented" "unpresented" "offered" "unoffered" 
   "given" "ungiven" "taken" "untaken" "received" "unreceived" "accepted" "unaccepted" "rejected" 
   "unrejected" "approved" "unapproved" "disapproved" "undisapproved" "endorsed" "unendorsed" 
   "supported" "unsupported" "backed" "unbacked" "helped" "unhelped" "assisted" "unassisted" 
   "aided" "unaided" "abetted" "unabetted" "encouraged" "unencouraged" "discouraged" 
   "undiscouraged" "motivated" "unmotivated" "inspired" "uninspired" "excited" "unexcited" 
   "stimulated" "unstimulated" "aroused" "unaroused" "awakened" "unawakened" "roused" "unroused" 
   "stirred" "unstirred" "moved" "unmoved" "touched" "untouched" "affected" "unaffected" 
   "influenced" "uninfluenced" "swayed" "unswayed" "persuaded" "unpersuaded" "convinced" 
   "unconvinced" "satisfied" "unsatisfied" "pleased" "unpleased" "displeased" "undispleased" 
   "happy" "unhappy" "sad" "unsad" "joyful" "unjoyful" "sorrowful" "unsorrowful" "mournful" 
   "unmournful" "grieving" "ungrieving" "lamenting" "unlamenting" "weeping" "unweeping" "crying" 
   "uncrying" "sobbing" "unsobbing" "wailing" "unwailing" "moaning" "unmoaning" "groaning" 
   "ungroaning" "sighing" "unsighing" "breathing" "unbreathing" "living" "unliving" "dead" 
   "undead" "alive" "unalive" "vital" "unvital" "vibrant" "unvibrant" "energetic" "unenergetic" 
   "lively" "unlively" "animated" "unanimated" "spirited" "unspirited" "vigorous" "unvigorous" 
   "robust" "unrobust" "healthy" "unhealthy" "well" "unwell" "ill" "unill" "sick" "unsick" 
   "diseased" "undiseased" "infected" "uninfected" "contagious" "uncontagious" "infectious" 
   "uninfectious" "virulent" "unvirulent" "deadly" "undeadly" "lethal" "unlethal" "fatal" 
   "unfatal" "mortal" "immortal" "deathly" "undeathly" "killing" "unkilling" "murderous" 
   "unmurderous" "homicidal" "unhomicidal" "suicidal" "unsuicidal" "destructive" "undestructive" 
   "damaging" "undamaging" "harmful" "unharmful" "injurious" "uninjurious" "hurtful" "unhurtful" 
   "painful" "unpainful" "agonizing" "unagonizing" "excruciating" "unexcruciating" "torturous" 
   "untorturous" "tormenting" "untormenting" "distressing" "undistressing" "troubling" 
   "untroubling" "worrying" "unworrying" "concerning" "unconcerning" "disturbing" "undisturbing" 
   "unsettling" "unusettling" "disquieting" "undisquieting" "alarming" "unalarming" "frightening" 
   "unfrightening" "scary" "unscary" "terrifying" "unterrifying" "horrifying" "unhorrifying" 
   "horrific" "unhorrifics"])

(def room-materials
  ["stone" "marble" "granite" "limestone" "sandstone" "slate" "brick" "wood" "timber" "metal" 
   "iron" "steel" "copper" "bronze" "brass" "gold" "silver" "crystal" "glass" "ice" "bone" 
   "obsidian" "clay" "mud" "dirt" "earth" "sand" "rock" "pebble" "gravel" "concrete" "plaster" 
   "stucco" "mortar" "cement" "adobe" "terracotta" "ceramic" "porcelain" "tile" "mosaic" 
   "ivory" "shell" "coral" "amber" "jade" "quartz" "diamond" "emerald" "ruby" "sapphire" 
   "opal" "pearl" "onyx" "agate" "jasper" "turquoise" "lapis lazuli" "malachite" "amethyst" 
   "topaz" "garnet" "aquamarine" "moonstone" "sunstone" "bloodstone" "hematite" "obsidian" 
   "flint" "chalk" "coal" "charcoal" "ash" "soot" "dust" "rust" "patina" "verdigris" "tarnish" 
   "moss" "lichen" "fungus" "mold" "mildew" "rot" "decay" "corruption" "putrefaction" 
   "decomposition" "dissolution" "disintegration" "erosion" "weathering" "wear" "tear" 
   "damage" "destruction" "ruin" "devastation" "desolation" "wasteland" "wilderness" "desert" 
   "tundra" "steppe" "prairie" "savanna" "grassland" "meadow" "field" "pasture" "lawn" 
   "garden" "park" "forest" "jungle" "rainforest" "swamp" "marsh" "bog" "fen" "mire" "quagmire" 
   "morass" "slough" "quicksand" "mud" "muck" "slime" "ooze" "goo" "gel" "jelly" "sap" "resin" 
   "amber" "pitch" "tar" "bitumen" "asphalt" "oil" "grease" "fat" "tallow" "wax" "soap" "foam" 
   "froth" "bubble" "spray" "mist" "fog" "haze" "smoke" "vapor" "steam" "gas" "air" "wind" 
   "breeze" "gust" "blast" "gale" "tempest" "storm" "hurricane" "tornado" "cyclone" "whirlwind" 
   "dust devil" "sandstorm" "blizzard" "snowstorm" "hailstorm" "thunderstorm" "lightning" 
   "thunder" "rain" "drizzle" "shower" "downpour" "deluge" "flood" "torrent" "stream" "river" 
   "brook" "creek" "tributary" "estuary" "delta" "lake" "pond" "pool" "puddle" "ocean" "sea" 
   "bay" "gulf" "strait" "channel" "sound" "fjord" "inlet" "cove" "harbor" "port" "dock" 
   "pier" "wharf" "jetty" "quay" "marina" "beach" "shore" "coast" "cliff" "bluff" "precipice" 
   "escarpment" "scarp" "ridge" "crest" "summit" "peak" "mountain" "hill" "mound" "knoll" 
   "dune" "valley" "dale" "glen" "gorge" "canyon" "ravine" "gully" "gulch" "arroyo" "wash" 
   "draw" "coulee" "hollow" "depression" "basin" "sink" "sinkhole" "pit" "abyss" "chasm" 
   "void" "vacuum" "emptiness" "nothingness" "oblivion" "limbo" "purgatory" "hell" "heaven" 
   "paradise" "utopia" "dystopia" "wasteland" "wilderness" "frontier" "border" "boundary" 
   "limit" "edge" "brink" "verge" "cusp" "threshold" "doorway" "gateway" "portal" "entrance" 
   "exit" "passage" "corridor" "hallway" "gallery" "arcade" "colonnade" "portico" "vestibule" 
   "foyer" "lobby" "atrium" "courtyard" "cloister" "quadrangle" "plaza" "square" "piazza" 
   "forum" "agora" "market" "bazaar" "souk" "mall" "arcade" "gallery" "museum" "library" 
   "archive" "repository" "vault" "treasury" "cache" "hoard" "stash" "store" "storehouse" 
   "warehouse" "depot" "terminal" "station" "port" "harbor" "haven" "refuge" "sanctuary" 
   "asylum" "retreat" "hideaway" "hideout" "lair" "den" "nest" "burrow" "hole" "cave" 
   "cavern" "grotto" "crypt" "tomb" "sepulcher" "mausoleum" "catacomb" "ossuary" "charnel" 
   "morgue" "mortuary" "cemetery" "graveyard" "churchyard" "burial ground" "potter's field" 
   "necropolis" "city of the dead" "underworld" "netherworld" "afterlife" "beyond" "hereafter" 
   "eternity" "infinity" "forever" "everlasting" "perpetual" "eternal" "immortal" "undying" 
   "deathless" "ageless" "timeless" "ancient" "primeval" "primordial" "prehistoric" "antediluvian" 
   "archaic" "classical" "medieval" "renaissance" "baroque" "rococo" "neoclassical" "romantic" 
   "victorian" "edwardian" "art nouveau" "art deco" "modernist" "postmodern" "contemporary" 
   "futuristic" "sci-fi" "cyberpunk" "steampunk" "dieselpunk" "atompunk" "biopunk" "nanopunk" 
   "solarpunk" "stonepunk" "bronzepunk" "ironpunk" "sandalpunk" "silkpunk" "clockpunk" 
   "mannerpunk" "mythpunk" "elfpunk" "fairypunk" "dreampunk" "nowpunk" "cattlepunk" "westernpunk" 
   "desertpunk" "oceanpunk" "icepunk" "islandpunk" "seapunk" "lunarpunk" "spacepunk" "astropunk" 
   "cosmicpunk" "godpunk" "angelicpunk" "demonpunk" "necropunk" "ghostpunk" "spiritpunk" 
   "soulpunk" "mindpunk" "psychicpunk" "magicpunk" "wizardpunk" "witchpunk" "alchemypunk" 
   "elementalpunk" "naturepunk" "treepunk" "flowerpunk" "gardenpunk" "farmerpunk" "foodpunk" 
   "winepunk" "beerpunk" "meadpunk" "honeypunk" "spicepunk" "herbpunk" "teapunk" "coffeepunk" 
   "chocolatepunk" "candypunk" "sugarpunk" "saltpunk" "pepperpunk" "vinegarpunk" "oilpunk" 
   "butterpunk" "cheesepunk" "milkpunk" "creampunk" "icecreampunk" "frozenpunk" "coldpunk" 
   "hotpunk" "firepunk" "flamepunk" "smokepunk" "ashpunk" "dustpunk" "sandpunk" "dirtpunk" 
   "mudpunk" "claypunk" "stonepunk" "rockpunk" "crystalpunk" "gemponk" "jewelpunk" "goldpunk" 
   "silverpunk" "copperpunk" "bronzepunk" "ironpunk" "steelpunk" "metalpunk" "glasspunk" 
   "woodpunk" "paperpunk" "cardboardpunk" "plasticpunk" "rubberpunk" "leatherpunk" "furpunk" 
   "woolpunk" "silkpunk" "cottonpunk" "linenpunk" "hemppunk" "jutepunk" "ropepunk" "stringpunk" 
   "threadpunk" "yarnpunk" "knitpunk" "crochetpunk" "sewpunk" "stitchpunk" "patchpunk" 
   "quiltpunk" "tapestrypunk" "rugpunk" "carpetpunk" "curtainpunk" "drapepunk" "clothpunk" 
   "fabricpunk" "textilepunk" "fashionpunk" "costumepunk" "uniformpunk" "armorpunk" "shieldpunk" 
   "helmetpunk" "swordpunk" "knifepunk" "daggerpunk" "axepunk" "hammerpunk" "maulpunk" 
   "flailpunk" "whippunk" "staffpunk" "wandpunk" "rodpunk" "polpunk" "spearponk" "lancepunk" 
   "pikepunk" "halberdpunk" "bowpunk" "arrowpunk" "crossbowpunk" "slingpunk" "stonepunk" 
   "rockpunk" "brickpunk" "throwpunk" "catchpunk" "ballpunk" "sportpunk" "gamepunk" "playpunk" 
   "toypunk" "dollpunk" "puppetpunk" "marionettepunk" "ventriloquistpunk" "clownpunk" 
   "jesterpunk" "foolpunk" "tricksterpunk" "pranksterpunk" "comedypunk" "tragedypunk" 
   "dramapunk" "theaterpunk" "cinemapunk" "moviepunk" "filmpunk" "videopunk" "tvpunk" 
   "radiopunk" "phonepunk" "telegraphpunk" "morsepunk" "signalpunk" "flagpunk" "semaphorepunk" 
   "drummingpunk" "whistlepunk" "hornpunk" "trumpetpunk" "buglpunk" "flutepunk" "pipepunk" 
   "organpunk" "pianopunk" "guitarponk" "violinpunk" "cellopunk" "harppunk" "lyrepunk" 
   "lutepunk" "mandolinpunk" "banjoponk" "ukuleleponk" "accordionpunk" "harmonicapunk" 
   "kazooponk" "whistlepunk" "singingpunk" "choirpunk" "operaponk" "symphonypunk" "orchestrapunk" 
   "bandpunk" "rockpunk" "poppunk" "jazzpunk" "bluespunk" "folkpunk" "countrypunk" "reggaepunk" 
   "hiphoppunk" "rappunk" "dancepunk" "ballroomponk" "balletpunk" "tappunk" "jazzpunk" 
   "swingpunk" "discpunk" "ravepunk" "clubpunk" "partypunk" "festivalpunk" "carnivalpunk" 
   "circuspunk" "fairpunk" "marketpunk" "bazaarpunk" "mallpunk" "shoppunk" "storepunk" 
   "boutiquepunk" "restaurantpunk" "cafepunk" "barpunk" "pubpunk" "tavernpunk" "innpunk" 
   "hotelpunk" "motelpunk" "bedandbreakfastpunk" "hostelpunk" "dormitorypunk" "apartmentpunk" 
   "condopunk" "housepunk" "mansionpunk" "palacepunk" "castlepunk" "fortresspunk" "towerpunk" 
   "dungeon"])

(def ceiling-descriptions
  ["The ceiling arches high overhead."
   "The ceiling is low, forcing you to stoop slightly."
   "The ceiling is vaulted with intricate ribbing."
   "The ceiling is domed and painted with faded murals."
   "The ceiling is flat and unremarkable."
   "The ceiling is supported by massive wooden beams."
   "The ceiling is barely visible in the darkness above."
   "The ceiling is covered in stalactites of various sizes."
   "The ceiling features a large, cracked skylight."
   "The ceiling is adorned with elaborate chandeliers."
   "The ceiling is a natural rock formation, uneven and rough."
   "The ceiling is decorated with intricate mosaic tiles."
   "The ceiling appears to be made of solid gold plates."
   "The ceiling is so high it disappears into shadows."
   "The ceiling is reinforced with iron supports."
   "The ceiling has partially collapsed in one corner."
   "The ceiling is covered in strange, glowing runes."
   "The ceiling is draped with ancient cobwebs."
   "The ceiling features a massive fresco depicting a battle."
   "The ceiling is studded with glittering gemstones."
   "The ceiling is made of interlocking stone blocks."
   "The ceiling is lined with copper pipes that occasionally drip."
   "The ceiling has been blackened by years of torch smoke."
   "The ceiling is perfectly smooth, as if polished."
   "The ceiling is etched with a detailed star map."
   "The ceiling is covered in a strange, pulsating fungus."
   "The ceiling is adorned with hundreds of small mirrors."
   "The ceiling is supported by columns carved to resemble trees."
   "The ceiling features a large, open trapdoor."
   "The ceiling is decorated with hanging crystal formations."
   "The ceiling is a chaotic jumble of exposed beams and supports."
   "The ceiling is covered in ancient, peeling paint."
   "The ceiling is inlaid with precious metals forming geometric patterns."
   "The ceiling appears to be made entirely of glass."
   "The ceiling is covered in strange carvings of unknown origin."
   "The ceiling is supported by massive stone pillars."
   "The ceiling is adorned with faded tapestries."
   "The ceiling is lined with rows of iron hooks."
   "The ceiling features a large, central oculus letting in light."
   "The ceiling is covered in a thin layer of ice crystals."
   "The ceiling is made of tightly fitted wooden planks."
   "The ceiling is decorated with bas-relief sculptures."
   "The ceiling is covered in a network of fine cracks."
   "The ceiling is adorned with hanging lanterns of various sizes."
   "The ceiling is unusually high for a room of this size."
   "The ceiling is covered in a strange, shifting illusion."
   "The ceiling is supported by flying buttresses along the walls."
   "The ceiling is decorated with intricate plasterwork."
   "The ceiling is covered in strange, chittering insects."
   "The ceiling is made of translucent crystal that glows faintly."])

(def wall-descriptions
  ["The walls are made of rough-hewn stone blocks."
   "The walls are covered in elaborate tapestries depicting ancient battles."
   "The walls are lined with ornate wooden paneling."
   "The walls are bare except for a few rusty chains hanging from iron hooks."
   "The walls are covered in strange, glowing runes."
   "The walls are adorned with faded frescoes of forgotten deities."
   "The walls are made of smooth marble with gold veining."
   "The walls are covered in a thick layer of moss and lichen."
   "The walls are lined with bookshelves filled with ancient tomes."
   "The walls are made of large granite blocks fitted perfectly together."
   "The walls are covered in strange hieroglyphics that seem to shift when not directly observed."
   "The walls are adorned with mounted weapons of various designs."
   "The walls are made of interlocking wooden timbers."
   "The walls are covered in a strange, pulsating fungus."
   "The walls are lined with alcoves containing small stone statues."
   "The walls are made of polished obsidian that reflects torchlight eerily."
   "The walls are covered in crude drawings made with what appears to be blood."
   "The walls are adorned with the mounted heads of various creatures."
   "The walls are made of large clay bricks, some of which have crumbled away."
   "The walls are covered in a thin layer of ice crystals that never seem to melt."
   "The walls are lined with empty torch sconces at regular intervals."
   "The walls are made of a strange, iridescent metal unknown to you."
   "The walls are covered in thousands of tiny scratches, as if made by claws."
   "The walls are adorned with delicate mosaics depicting pastoral scenes."
   "The walls are made of massive blocks of sandstone, worn smooth by time."
   "The walls are covered in a thick layer of soot from countless torches."
   "The walls are lined with empty weapon racks."
   "The walls are made of a strange, semi-transparent crystal."
   "The walls are covered in elaborate carvings of intertwining serpents."
   "The walls are adorned with faded banners bearing unknown heraldry."
   "The walls are made of tightly fitted stones without mortar."
   "The walls are covered in a strange, sticky substance that glistens in the light."
   "The walls are lined with niches containing urns of various sizes."
   "The walls are made of a dark wood that seems to absorb light."
   "The walls are covered in ancient graffiti in dozens of different languages."
   "The walls are adorned with copper plates that have turned green with age."
   "The walls are made of large blocks of white marble streaked with black."
   "The walls are covered in a network of fine cracks that seem to form patterns."
   "The walls are lined with empty bird cages of various sizes."
   "The walls are made of a strange, spongy material that gives slightly when touched."
   "The walls are covered in elaborate geometric patterns inlaid with silver."
   "The walls are adorned with mirrors of various sizes, most of them cracked."
   "The walls are made of rough-hewn logs stacked horizontally."
   "The walls are covered in a layer of dust so thick it obscures their true nature."
   "The walls are lined with shelves containing hundreds of small glass bottles."
   "The walls are made of a strange, metallic substance that hums faintly."
   "The walls are covered in what appears to be human handprints in various colors."
   "The walls are adorned with intricate clockwork mechanisms that tick quietly."
   "The walls are made of large blocks of jade, cool to the touch."
   "The walls are covered in a strange script that glows faintly in the dark."])

(def floor-descriptions
  ["The floor is made of smooth, polished marble."
   "The floor is covered in rough, uneven flagstones."
   "The floor is a mosaic of colorful tiles forming an intricate pattern."
   "The floor is made of packed earth, hard as stone from countless footsteps."
   "The floor is wooden, with planks that creak underfoot."
   "The floor is covered in a thick layer of dust that shows no footprints."
   "The floor is made of hexagonal stone tiles fitted perfectly together."
   "The floor is covered in a plush, albeit moldy, carpet."
   "The floor is made of large slate tiles with strange symbols etched into them."
   "The floor is covered in a thin layer of standing water that reflects the ceiling."
   "The floor is made of polished obsidian that reflects like a dark mirror."
   "The floor is covered in small, rounded pebbles that shift underfoot."
   "The floor is made of large bronze plates that ring hollowly when stepped on."
   "The floor is covered in a strange, luminescent moss that glows faintly."
   "The floor is made of tightly fitted wooden parquet in an elaborate pattern."
   "The floor is covered in a fine layer of ash that swirls with each step."
   "The floor is made of large granite blocks with deep grooves between them."
   "The floor is covered in a thick layer of straw that rustles when disturbed."
   "The floor is made of small ceramic tiles in alternating black and white."
   "The floor is covered in a strange, sticky substance that pulls at your boots."
   "The floor is made of packed sand that shifts slightly underfoot."
   "The floor is covered in a thin layer of ice that cracks but doesn't break."
   "The floor is made of interlocking metal plates with small holes."
   "The floor is covered in a thick layer of fallen leaves that crunch underfoot."
   "The floor is made of large glass blocks that glow with an inner light."
   "The floor is covered in a fine layer of salt that crunches with each step."
   "The floor is made of tightly woven reeds that spring back when stepped on."
   "The floor is covered in a thin layer of fine, red powder."
   "The floor is made of large copper plates that have turned green with age."
   "The floor is covered in a thick layer of soft, yielding clay."
   "The floor is made of tightly fitted stones arranged in concentric circles."
   "The floor is covered in a strange, shifting pattern that seems to move when not directly observed."
   "The floor is made of large wooden boards held together with iron nails."
   "The floor is covered in a thin layer of fine, black sand."
   "The floor is made of small river stones set in mortar."
   "The floor is covered in a thick layer of bone dust that puffs up with each step."
   "The floor is made of large alabaster tiles veined with gold."
   "The floor is covered in a strange, spongy fungus that compresses underfoot."
   "The floor is made of tightly fitted bricks arranged in a herringbone pattern."
   "The floor is covered in a thin layer of fine, glittering powder."
   "The floor is made of large sandstone blocks worn smooth by countless feet."
   "The floor is covered in a thick layer of small, polished gemstones."
   "The floor is made of tightly woven metal wires forming a flexible surface."
   "The floor is covered in a thin layer of fine, white powder that clings to everything."
   "The floor is made of large jade tiles that feel cool underfoot."
   "The floor is covered in a thick layer of fine, golden sand."
   "The floor is made of tightly fitted wooden hexagons in various dark woods."
   "The floor is covered in a strange, oily substance that reflects like a mirror."
   "The floor is made of large crystal blocks that seem to amplify sound."
   "The floor is covered in a thin layer of fine, blue powder that stains when touched."])

(def atmosphere-descriptions
  ["The air is thick with the smell of damp stone and mold."
   "A chill breeze seems to come from nowhere, carrying whispers just beyond hearing."
   "The room is unnaturally silent, as if sound itself is swallowed by the walls."
   "The air is heavy with the scent of ancient incense and dust."
   "A strange humming vibration can be felt through the floor."
   "The atmosphere is oppressively hot and humid, making it difficult to breathe."
   "The air tastes metallic, like blood or iron."
   "A sense of profound sadness permeates the space, weighing on your heart."
   "The room feels charged with static electricity, making hair stand on end."
   "The air is surprisingly fresh and clean, unlike the rest of the dungeon."
   "A feeling of being watched prickles at the back of your neck."
   "The atmosphere is supernaturally cold, causing breath to fog."
   "The air shimmers slightly, as if reality itself is thin here."
   "A faint, rhythmic sound like a distant heartbeat pulses through the room."
   "The space feels unnaturally pressurized, as if deep underwater."
   "A sense of vertigo grips you, despite standing on solid ground."
   "The air carries a sweet, cloying scent that makes thinking difficult."
   "Time seems to move differently here, either too quickly or too slowly."
   "A feeling of peace and safety, completely out of place, fills the room."
   "The atmosphere is charged with a sense of imminent danger."
   "The air is filled with dancing motes of light that vanish when looked at directly."
   "A sense of profound age and history presses down from all sides."
   "The room seems to pulse with a life of its own, as if breathing slowly."
   "The air tastes of ozone, like after a lightning strike."
   "A feeling of being unwelcome, of trespassing, is impossible to shake."
   "The atmosphere is thick with the scent of exotic spices and strange herbs."
   "A sense of deja vu is overwhelming, as if you've been here before."
   "The air feels thick, making movement slightly more difficult than it should be."
   "A strange euphoria fills the room, causing unwarranted happiness."
   "The atmosphere is charged with magical energy that makes your skin tingle."
   "A sense of profound loss and mourning fills the space."
   "The air is completely still, not a single current or draft to be felt."
   "A feeling of being diminished, of being less than you were, permeates the room."
   "The atmosphere is filled with a barely audible, high-pitched ringing."
   "A sense of reverence, as if in a sacred space, fills you unexpectedly."
   "The air smells strongly of earth and roots, like a freshly dug grave."
   "A feeling of being stretched thin, of being in multiple places at once, is disorienting."
   "The atmosphere is heavy with the scent of old leather and parchment."
   "A sense of time standing still, of being caught in a single moment, is palpable."
   "The air tastes faintly sweet, like honey or nectar."
   "A feeling of being judged by unseen eyes follows your every move."
   "The atmosphere is charged with the energy of a coming storm."
   "A sense of having forgotten something important nags at your mind."
   "The air carries the distant sound of children laughing, though none are present."
   "A feeling of homesickness for a place you've never been overwhelms you."
   "The atmosphere is thick with the smell of burning candles and old wax."
   "A sense of being on the precipice of a great discovery fills you with anticipation."
   "The air feels thick with unspoken words and secrets."
   "A feeling of being exactly where you are meant to be, of destiny, fills the room."
   "The atmosphere is charged with the residual energy of powerful magic once cast here."])

(def light-descriptions
  ["The room is pitch black, with no source of light visible."
   "A single torch flickers in a wall sconce, casting long shadows."
   "Pale blue light emanates from glowing crystals embedded in the walls."
   "Shafts of dusty sunlight stream through small openings high in the walls."
   "The room is illuminated by a chandelier holding dozens of candles."
   "Strange, phosphorescent fungi provide a soft, green glow."
   "A magical orb floats in the center of the room, radiating warm light."
   "Oil lamps with colored glass cast rainbow patterns across the floor."
   "The room is lit by an eerie, sourceless light that seems to come from everywhere and nowhere."
   "Braziers filled with glowing coals provide both light and warmth."
   "Moonlight streams through a large skylight, painting everything in silver."
   "Glowing runes carved into the walls pulse with rhythmic light."
   "A large fireplace dominates one wall, its flames casting flickering shadows."
   "Small, magical lights like fireflies drift lazily through the air."
   "The room is illuminated by a strange, purple light with no discernible source."
   "Tall candelabras stand in each corner, their candles burning with an unnatural steadiness."
   "Light filters through stained glass windows, creating colorful patterns."
   "A beam of intense white light shines down from a hole in the ceiling."
   "The room is lit by glowing pools of mysterious liquid scattered across the floor."
   "Floating lanterns drift near the ceiling, swaying slightly as if in a breeze."
   "The walls themselves seem to glow with a soft, amber light."
   "A single beam of sunlight cuts through the darkness, illuminating dancing dust motes."
   "The room is lit by strange, glowing insects trapped in glass jars."
   "Pools of radiant energy gather in the corners, pulsing gently."
   "A large crystal in the center of the room refracts light in all directions."
   "The room is illuminated by a crackling ball of lightning contained in a glass sphere."
   "Ghostly, blue-white flames burn in iron sconces without fuel."
   "Light seeps through cracks in the ceiling, creating dappled patterns."
   "The room is lit by a series of mirrors that reflect and amplify a single light source."
   "Glowing, molten metal flows through channels in the walls, providing orange-red light."
   "The room is illuminated by a strange, shifting aurora that plays across the ceiling."
   "Light radiates from a pool of luminescent water in the center of the room."
   "Bioluminescent vines crawl across the walls, giving off a soft, blue glow."
   "The room is lit by hovering spheres of magical light that follow movement."
   "A strange, pulsing light emanates from beneath the floor, visible through cracks."
   "The room is illuminated by a miniature sun contained within a complex apparatus."
   "Light filters through crystal prisms, casting rainbow spectrums across the walls."
   "Glowing embers float through the air like lazy fireflies, providing dim illumination."
   "The room is lit by strange fungi that glow brighter when approached."
   "A pillar of light descends from above, illuminating a circular area in the center."
   "The room is illuminated by bottles of glowing liquid arranged on shelves."
   "Light radiates from a series of enchanted weapons mounted on the walls."
   "Glowing symbols float in the air, providing a soft, shifting illumination."
   "The room is lit by a large, glowing egg-shaped object on a pedestal."
   "Light pulses from veins of strange ore embedded in the walls."
   "The room is illuminated by what appears to be captured stars in glass containers."
   "A glowing mist hovers near the ceiling, providing diffuse light."
   "The room is lit by a series of magical flames that burn in different colors."
   "Light emanates from a swirling vortex of energy contained within a ring of stones."
   "The room is illuminated by the ghostly glow of spectral entities that drift aimlessly."])

(def sound-descriptions
  ["The room is completely silent, unnaturally so."
   "A steady dripping of water echoes from somewhere unseen."
   "The distant sound of chanting can be heard, though the words are indistinguishable."
   "A low, mechanical humming vibrates through the floor and walls."
   "The whisper of a gentle breeze can be heard, though the air is still."
   "Occasional creaks and groans, as if the room itself is settling."
   "The faint sound of chains rattling comes and goes unpredictably."
   "A rhythmic tapping, like a heartbeat, seems to come from all directions."
   "The distant sound of screaming occasionally pierces the silence."
   "A soft, melodic tune plays from an unseen music box."
   "The chittering of unseen creatures scurrying within the walls."
   "A low, barely audible moaning that seems to follow you around the room."
   "The sound of distant footsteps that stop whenever you do."
   "A faint, ghostly whispering that seems to be just behind your ear."
   "The occasional sound of glass breaking, though nothing visible shatters."
   "A deep, resonant tone that pulses at irregular intervals."
   "The sound of pages turning in a book, though none are visible."
   "A faint, childlike giggling that seems to come from the shadows."
   "The distant sound of a battle, with clashing metal and shouted commands."
   "A soft, continuous hissing, like steam escaping from a valve."
   "The occasional sound of a door slamming shut in the distance."
   "A rhythmic clicking, like someone tapping their fingernails on stone."
   "The sound of distant thunder that seems to be drawing closer."
   "A faint melody played on an unseen stringed instrument."
   "The sound of someone breathing heavily, though you are alone."
   "A continuous, low rumbling, as if from some massive beast."
   "The occasional sound of furniture being moved in the room above."
   "A high-pitched ringing that comes and goes at random intervals."
   "The sound of waves crashing against a shore, though no water is visible."
   "A faint ticking, like countless clocks counting down in unison."
   "The distant sound of a crowd cheering, then suddenly falling silent."
   "A soft, continuous scratching, as if something is trying to get in—or out."
   "The occasional sound of a bell tolling in the distance."
   "A faint buzzing, like a swarm of insects just out of sight."
   "The sound of someone crying softly, the source impossible to locate."
   "A rhythmic drumming that seems to match your heartbeat."
   "The distant sound of a woman singing a melancholy lullaby."
   "A continuous creaking, like an old ship at sea."
   "The sound of glass wind chimes tinkling in a non-existent breeze."
   "A faint, rhythmic chanting in an unknown language."
   "The occasional sound of something heavy being dragged across stone."
   "A soft, continuous murmuring, as if the walls themselves are speaking."
   "The sound of a music box playing a discordant, unsettling tune."
   "A faint, rhythmic pounding, like a blacksmith at work far away."
   "The sound of someone running, their footsteps circling the room."
   "A continuous, soft rustling, like countless pieces of parchment being shuffled."
   "The occasional sound of a child counting slowly, then starting over."
   "A faint, melodic humming that seems to follow you around the room."
   "The sound of distant machinery, grinding and clanking."
   "A soft, continuous sound like sand falling through an hourglass."])

(def feature-descriptions
  ["A large crack runs across the floor, too wide to step over easily."
   "Strange symbols are carved into the center of the floor, forming a perfect circle."
   "A life-sized statue of a warrior stands in one corner, its stone eyes seeming to follow movement."
   "A fountain burbles in the center of the room, its water an unusual color."
   "A massive crystal protrudes from one wall, pulsing with inner light."
   "An ornate sarcophagus rests on a raised dais in the center of the room."
   "A mysterious altar stands before the far wall, stained with something dark."
   "A large mirror hangs on one wall, but it reflects a room different from this one."
   "A spiral staircase in one corner leads both up and down into darkness."
   "A massive, ancient tree grows in the center of the room, its branches touching the ceiling."
   "A deep pit occupies the center of the floor, its bottom lost in darkness."
   "A complex mechanical device of unknown purpose dominates the room."
   "A massive throne carved from a single piece of stone sits on a raised platform."
   "A large pendulum swings slowly back and forth across the room."
   "A magical circle inscribed on the floor glows faintly with arcane energy."
   "A series of freestanding archways cross the room, each seemingly leading to a different location when viewed from certain angles."
   "A large, ornate cage hangs from the ceiling, big enough to hold a person."
   "A pool of still, reflective liquid that doesn't appear to be water fills a basin in the center."
   "A massive skeleton of an unknown creature is embedded in one wall."
   "A large hourglass stands in the center of the room, its sand flowing upward instead of down."
   "A series of pedestals hold objects covered in velvet cloths."
   "A large map is carved into the floor, depicting lands you've never seen."
   "A massive orrery shows the movements of planets and stars unknown to you."
   "A wall of flowing water runs down one side of the room, but defies gravity by not pooling on the floor."
   "A large, ornate clock with unfamiliar symbols instead of numbers dominates one wall."
   "A series of floating stepping stones cross a misty chasm that splits the room."
   "A massive book lies open on a pedestal, its pages turning by themselves."
   "A large, circular portal frame stands empty in the center of the room."
   "A series of life-sized chess pieces are arranged as if in the middle of a game."
   "A massive crystal chandelier lies shattered on the floor, though the ceiling is intact."
   "A large, ornate carpet covers the floor, its pattern shifting subtly when not directly observed."
   "A series of mirrors arranged in a circle reflect infinitely into each other."
   "A massive globe of an unfamiliar world rotates slowly on a pedestal."
   "A large sundial occupies the center of the room, though there is no sun to cast a shadow."
   "A series of transparent tubes run along the walls, filled with flowing, colored liquids."
   "A massive loom stands in one corner, a half-finished tapestry of strange design still attached."
   "A large, ornate bathtub filled with a strange, glowing liquid dominates the center of the room."
   "A series of glass cases contain preserved specimens of creatures you've never seen before."
   "A massive telescope points at a specific part of the ceiling, which appears to show the night sky."
   "A large, ornate door stands in the center of the room, unconnected to any wall."
   "A series of floating candles drift around the room, moving in complex patterns."
   "A massive chessboard is inlaid into the floor, with pieces the size of small children."
   "A large, ornate birdcage contains a mechanical bird that occasionally sings."
   "A series of columns rise from floor to ceiling, each carved to resemble a different creature."
   "A massive web fills one corner of the room, though no spider is visible."
   "A large, ornate music box sits open on a pedestal, its cylinder slowly turning."
   "A series of stained glass windows depict a story you don't recognize."
   "A massive forge dominates one wall, its fire burning without fuel."
   "A large, ornate bed with dusty hangings suggests this room once served as living quarters."
   "A series of alcoves line the walls, each containing a different strange object."])

;; Custom format function for string formatting (since format is not available in ClojureScript by default)
(defn format-str [fmt & args]
  (let [fmt-str (js/String. fmt)]
    (loop [formatted fmt-str
           args-left args
           index 0]
      (if (empty? args-left)
        formatted
        (let [placeholder (str "%s")
              next-index (.indexOf formatted placeholder index)]
          (if (neg? next-index)
            formatted
            (recur
              (str (.substring formatted 0 next-index) 
                   (first args-left) 
                   (.substring formatted (+ next-index (count placeholder))))
              (rest args-left)
              (+ next-index (count (str (first args-left)))))))))))

(defn generate-room-description []
  (let [templates [;; Basic templates
                   "You enter a %s %s room with %s walls, %s floor, and %s. %s"
                   "Before you lies a %s %s chamber. %s %s %s"
                   "The passage opens into a %s %s hall with %s. %s %s"
                   "You find yourself in a %s %s chamber. %s %s %s"
                   "A %s %s room stretches before you. %s %s %s"
                   
                   ;; More complex templates
                   "You step into a %s, %s room. %s The %s walls surround a %s floor. %s"
                   "The corridor leads to a %s %s chamber with %s walls and %s floor. %s %s"
                   "A %s, %s room extends ahead. %s %s %s"
                   "You enter a %s chamber that appears to be %s in shape. %s %s %s"
                   "Before you is a %s, %s hall. %s The %s floor stretches beneath %s walls. %s"
                   
                   ;; Templates with features
                   "You discover a %s %s room. %s %s %s %s"
                   "A %s, %s chamber opens before you. %s %s %s %s"
                   "You enter a %s room that is roughly %s in shape. %s %s %s %s"
                   "The passage reveals a %s, %s chamber. %s %s %s %s"
                   "A %s %s room lies ahead. %s %s %s %s"]
        
        template (rand-nth templates)
        
        size (rand-nth room-sizes)
        shape (rand-nth room-shapes)
        adjective (rand-nth room-adjectives)
        material (rand-nth room-materials)
        ceiling (rand-nth ceiling-descriptions)
        wall (rand-nth wall-descriptions)
        floor (rand-nth floor-descriptions)
        atmosphere (rand-nth atmosphere-descriptions)
        light (rand-nth light-descriptions)
        sound (rand-nth sound-descriptions)
        feature (rand-nth feature-descriptions)
        
        ;; Select random elements to fill in the template
        elements (case (count (re-seq #"%s" template))
                   3 [size shape wall]
                   4 [size shape wall floor]
                   5 [size shape wall floor atmosphere]
                   6 [size shape wall floor atmosphere feature]
                   ;; Default case
                   [size shape adjective material ceiling])]
    
    (apply format-str template elements)))

(defn app []
  (let [embed-mode (:embed-mode @state)]
    [:main {:class (when embed-mode "embed-mode")}
     (when-not embed-mode
       [:header
        [:div.title [:a {:href "../" :style {:color "inherit" :text-decoration "none"}} "Fantasy Generators"]]
        [:nav
         [:a {:href "mailto:chris@mccormick.cx"} "Contact"]]])

     (when-not embed-mode
       [:h1 "Dungeon Room Generator"])

     [:div.generator-container
      (if-let [description (:room-description @state)]
        [:div.room-description description]
        [:div.room-description "Click the button to generate a dungeon room description."])
      [:div.generator-buttons
       [:button
        {:on-click #(swap! state assoc :room-description (generate-room-description))}
        "Generate New Room"]]
      (when-not embed-mode
        [:div.attribution "A procedurally generated dungeon room description"])
      (when-not embed-mode
        [:div.embed-link
         [:button.embed-btn-small
          {:on-click (fn []
                       (copy-to-clipboard (str (.-origin js/location) (.-pathname js/location) "?embed"))
                       (swap! state assoc :copied true)
                       (js/setTimeout #(swap! state assoc :copied false) 2000))}
          (if (:copied @state) "Copied!" "Embed")]])]

     (when-not embed-mode
       [:a.back-link {:href "../index.html"} "← Back to generators"])

     (when-not embed-mode
       [:footer [:a {:href "https://mccormick.cx" :style {:color "#5d1a0f" :text-decoration "none" :font-weight "bold"}} "Made with 🤖 by Chris McCormick"]])
     (when-not embed-mode
       [:div.footer-bg])]))

;; Initialize with a random room description
(defn init []
  (swap! state assoc :room-description (generate-room-description))
  (rdom/render [app] (.getElementById js/document "app")))

;; Call init function
(init)
