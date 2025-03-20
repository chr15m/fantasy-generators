(ns main
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(defonce state (r/atom {:character nil
                        :embed-mode (boolean (re-find #"[?&]embed" (.-search js/location)))
                        :copied false}))

(defn copy-to-clipboard [text]
  (let [el (.createElement js/document "textarea")]
    (set! (.-value el) text)
    (.appendChild (.-body js/document) el)
    (.select el)
    (.execCommand js/document "copy")
    (.removeChild (.-body js/document) el)))

;; Character generation data based on the tables
(def age-table
  [{:roll 1 :age "10 and under"}
   {:roll 2 :age "11-20 years old"}
   {:roll 3 :age "21-30 years old"}
   {:roll 4 :age "31-40 years old"}
   {:roll 5 :age "41-50 years old"}
   {:roll 6 :age "51+ years old"}])

(def race-table
  [{:roll 1 :race "Human"}
   {:roll 2 :race "Half-Elf"}
   {:roll 3 :race "Half-Orc"}
   {:roll 4 :race "Halfling"}
   {:roll 5 :race "Tiefling"}
   {:roll 6 :race "Gnome"}
   {:roll 7 :race "Elf"}
   {:roll 8 :race "Dwarf"}
   {:roll 9 :race "Dragonborn"}
   {:roll 10 :race "Other"}])

(def hair-color-table
  [{:roll 1 :color "Black"}
   {:roll 2 :color "Dark Brown"}
   {:roll 3 :color "Chestnut Brown"}
   {:roll 4 :color "Light Brown"}
   {:roll 5 :color "Auburn"}
   {:roll 6 :color "Ginger"}
   {:roll 7 :color "Dark Blonde"}
   {:roll 8 :color "Golden Blonde"}
   {:roll 9 :color "Flaxen"}
   {:roll 10 :color "Gray"}
   {:roll 11 :color "Salt & Pepper"}
   {:roll 12 :color "White"}])

(def eye-color-table
  [{:roll 1 :color "Dark Brown"}
   {:roll 2 :color "Chestnut Brown"}
   {:roll 3 :color "Light Brown"}
   {:roll 4 :color "Hazel"}
   {:roll 5 :color "Blue-Gray"}
   {:roll 6 :color "Blue-green"}
   {:roll 7 :color "Green"}
   {:roll 8 :color "Amber"}
   {:roll 9 :color "Black"}
   {:roll 10 :color "Unnatural"}])

(def hair-feature-table
  [{:roll 1 :feature "Dyed"}
   {:roll 2 :feature "Bangs"}
   {:roll 3 :feature "Greying"}
   {:roll 4 :feature "Balding/Patchy"}
   {:roll 5 :feature "Damaged"}
   {:roll 6 :feature "Thinning"}
   {:roll 7 :feature "Pulled back"}
   {:roll 8 :feature "Unkempt"}
   {:roll 9 :feature "Styled"}
   {:roll 10 :feature "Partly Shaven"}
   {:roll 11 :feature "Uneven"}
   {:roll 12 :feature "Wig"}])

(def hair-type-table
  [{:roll 1 :type "Thick"}
   {:roll 2 :type "Curly"}
   {:roll 3 :type "Fine"}
   {:roll 4 :type "Straight"}
   {:roll 5 :type "Full"}
   {:roll 6 :type "Glossy"}
   {:roll 7 :type "Dull"}
   {:roll 8 :type "Frizzy"}
   {:roll 9 :type "Silky"}
   {:roll 10 :type "Wavy"}
   {:roll 11 :type "Bouncy"}
   {:roll 12 :type "Flat"}])

(def hair-length-table
  [{:roll 1 :length "Very Short"}
   {:roll 2 :length "Short"}
   {:roll 3 :length "Shoulder Length"}
   {:roll 4 :length "Mid-back"}
   {:roll 5 :length "Hip Length"}
   {:roll 6 :length "Pulled Back"}
   {:roll 7 :length "Braided"}
   {:roll 8 :length "Pulled Up"}
   {:roll 9 :length "Shaved"}
   {:roll 10 :length "Thigh-length+"}])

(def motivation-table
  [{:roll 1 :motivation "Survival"}
   {:roll 2 :motivation "Love"}
   {:roll 3 :motivation "Honor"}
   {:roll 4 :motivation "Control"}
   {:roll 5 :motivation "Fame"}
   {:roll 6 :motivation "Serve"}
   {:roll 7 :motivation "Hide"}
   {:roll 8 :motivation "Destroy"}
   {:roll 9 :motivation "Greed"}
   {:roll 10 :motivation "Betrayal"}
   {:roll 11 :motivation "Fear"}
   {:roll 12 :motivation "Escape"}
   {:roll 13 :motivation "Revenge"}
   {:roll 14 :motivation "Recover"}
   {:roll 15 :motivation "Justice"}
   {:roll 16 :motivation "Desire"}
   {:roll 17 :motivation "Discover"}
   {:roll 18 :motivation "Achieve"}
   {:roll 19 :motivation "Hate"}
   {:roll 20 :motivation "Ambition"}])

(def class-table
  [{:roll 1 :class "Barbarian"}
   {:roll 2 :class "Bard"}
   {:roll 3 :class "Cleric"}
   {:roll 4 :class "Druid"}
   {:roll 5 :class "Fighter"}
   {:roll 6 :class "Monk"}
   {:roll 7 :class "Paladin"}
   {:roll 8 :class "Ranger"}
   {:roll 9 :class "Rogue"}
   {:roll 10 :class "Sorcerer"}
   {:roll 11 :class "Warlock"}
   {:roll 12 :class "Wizard"}])

(def alignment-table
  [{:roll 1 :alignment "Lawful Good"}
   {:roll 2 :alignment "Neutral Good"}
   {:roll 3 :alignment "Chaotic Good"}
   {:roll 4 :alignment "Lawful Neutral"}
   {:roll 5 :alignment "True Neutral"}
   {:roll 6 :alignment "Chaotic Neutral"}
   {:roll 7 :alignment "Lawful Evil"}
   {:roll 8 :alignment "Neutral Evil"}
   {:roll 9 :alignment "Chaotic Evil"}])

(def archetype-table
  [{:roll 1 :archetype "The Hero"}
   {:roll 2 :archetype "The Alchemist"}
   {:roll 3 :archetype "The Lover"}
   {:roll 4 :archetype "The Jester"}
   {:roll 5 :archetype "The Everyperson"}
   {:roll 6 :archetype "The Innocent"}
   {:roll 7 :archetype "The Sage"}
   {:roll 8 :archetype "The Explorer"}
   {:roll 9 :archetype "The Caregiver"}
   {:roll 10 :archetype "The Creator"}
   {:roll 11 :archetype "The Ruler"}
   {:roll 12 :archetype "The Rebel"}])

(def trait-table-1
  [{:roll 1 :trait "Kind"}
   {:roll 2 :trait "Rude"}
   {:roll 3 :trait "Humble"}
   {:roll 4 :trait "Arrogant"}
   {:roll 5 :trait "Gentle"}
   {:roll 6 :trait "Cruel"}
   {:roll 7 :trait "Polite"}
   {:roll 8 :trait "Blunt"}
   {:roll 9 :trait "Outgoing"}
   {:roll 10 :trait "Reserved"}
   {:roll 11 :trait "Cheerful"}
   {:roll 12 :trait "Brooding"}
   {:roll 13 :trait "Sensitive"}
   {:roll 14 :trait "Callous"}
   {:roll 15 :trait "Easygoing"}
   {:roll 16 :trait "Aggressive"}
   {:roll 17 :trait "Energetic"}
   {:roll 18 :trait "Quiet"}
   {:roll 19 :trait "Patient"}
   {:roll 20 :trait "Rash"}])

(def trait-table-2
  [{:roll 1 :trait "Loyal"}
   {:roll 2 :trait "Two-Faced"}
   {:roll 3 :trait "Brave"}
   {:roll 4 :trait "Cowardly"}
   {:roll 5 :trait "Honest"}
   {:roll 6 :trait "Liar"}
   {:roll 7 :trait "Generous"}
   {:roll 8 :trait "Selfish"}
   {:roll 9 :trait "Diligent"}
   {:roll 10 :trait "Careless"}
   {:roll 11 :trait "Responsible"}
   {:roll 12 :trait "Unreliable"}
   {:roll 13 :trait "Shy"}
   {:roll 14 :trait "Impulsive"}
   {:roll 15 :trait "Attentive"}
   {:roll 16 :trait "Reckless"}
   {:roll 17 :trait "Frugal"}
   {:roll 18 :trait "Extravagant"}
   {:roll 19 :trait "Mischievous"}
   {:roll 20 :trait "Obedient"}])

(def trait-table-3
  [{:roll 1 :trait "Open-Minded"}
   {:roll 2 :trait "Prejudice"}
   {:roll 3 :trait "Creative"}
   {:roll 4 :trait "Cunning"}
   {:roll 5 :trait "Decisive"}
   {:roll 6 :trait "Indecisive"}
   {:roll 7 :trait "Wise"}
   {:roll 8 :trait "Naive"}
   {:roll 9 :trait "Sincere"}
   {:roll 10 :trait "Sarcastic"}
   {:roll 11 :trait "Orderly"}
   {:roll 12 :trait "Messy"}
   {:roll 13 :trait "Hardworking"}
   {:roll 14 :trait "Lazy"}
   {:roll 15 :trait "Mature"}
   {:roll 16 :trait "Immature"}
   {:roll 17 :trait "Modest"}
   {:roll 18 :trait "Vain"}
   {:roll 19 :trait "Persistent"}
   {:roll 20 :trait "Meek"}])

(def trait-table-4
  [{:roll 1 :trait "Intelligent"}
   {:roll 2 :trait "Ignorant"}
   {:roll 3 :trait "Assertive"}
   {:roll 4 :trait "Hesitant"}
   {:roll 5 :trait "Spoiled"}
   {:roll 6 :trait "Cautious"}
   {:roll 7 :trait "Reasonable"}
   {:roll 8 :trait "Stubborn"}
   {:roll 9 :trait "Emotional"}
   {:roll 10 :trait "Apathetic"}
   {:roll 11 :trait "Funny"}
   {:roll 12 :trait "Serious"}
   {:roll 13 :trait "Charming"}
   {:roll 14 :trait "Moody"}
   {:roll 15 :trait "Independent"}
   {:roll 16 :trait "Dependent"}
   {:roll 17 :trait "Determined"}
   {:roll 18 :trait "Petty"}
   {:roll 19 :trait "Pious"}
   {:roll 20 :trait "Paranoid"}])

(def proficiency-table
  [{:roll 1 :proficiency "Acrobatics"}
   {:roll 2 :proficiency "Animal Handling"}
   {:roll 3 :proficiency "Arcana"}
   {:roll 4 :proficiency "Investigation"}
   {:roll 5 :proficiency "Medicine"}
   {:roll 6 :proficiency "Nature"}
   {:roll 7 :proficiency "Perception"}
   {:roll 8 :proficiency "Religion"}])

(def profession-table
  [{:roll 1 :profession "Farmer"}
   {:roll 2 :profession "Armourer"}
   {:roll 3 :profession "Butcher"}
   {:roll 4 :profession "Blacksmith"}
   {:roll 5 :profession "Barber"}
   {:roll 6 :profession "Weaver"}
   {:roll 7 :profession "Miller"}
   {:roll 8 :profession "Baker"}
   {:roll 9 :profession "Porter"}
   {:roll 10 :profession "Thatcher"}
   {:roll 11 :profession "Mason"}
   {:roll 12 :profession "Carpenter"}
   {:roll 13 :profession "Peddler"}
   {:roll 14 :profession "Fisherman"}
   {:roll 15 :profession "Miner"}
   {:roll 16 :profession "Tanner"}
   {:roll 17 :profession "Brewer"}
   {:roll 18 :profession "Barkeep"}
   {:roll 19 :profession "Candlemaker"}
   {:roll 20 :profession "Merchant"}])

(defn random-int [max]
  (inc (js/Math.floor (* (js/Math.random) max))))

(defn get-table-result [table]
  (let [max-roll (count table)
        roll (random-int max-roll)
        result (first (filter #(= (:roll %) roll) table))]
    (or result (first table))))

(defn generate-character []
  (let [age (:age (get-table-result age-table))
        race (:race (get-table-result race-table))
        hair-color (:color (get-table-result hair-color-table))
        eye-color (:color (get-table-result eye-color-table))
        hair-feature (:feature (get-table-result hair-feature-table))
        hair-type (:type (get-table-result hair-type-table))
        hair-length (:length (get-table-result hair-length-table))
        motivation (:motivation (get-table-result motivation-table))
        class (:class (get-table-result class-table))
        alignment (:alignment (get-table-result alignment-table))
        archetype (:archetype (get-table-result archetype-table))
        trait-1 (:trait (get-table-result trait-table-1))
        trait-2 (:trait (get-table-result trait-table-2))
        trait-3 (:trait (get-table-result trait-table-3))
        trait-4 (:trait (get-table-result trait-table-4))
        proficiency (:proficiency (get-table-result proficiency-table))
        profession (:profession (get-table-result profession-table))]
    {:age age
     :race race
     :hair-color hair-color
     :eye-color eye-color
     :hair-feature hair-feature
     :hair-type hair-type
     :hair-length hair-length
     :motivation motivation
     :class class
     :alignment alignment
     :archetype archetype
     :traits [trait-1 trait-2 trait-3 trait-4]
     :proficiency proficiency
     :profession profession}))

(defn character-trait [label value]
  [:div.character-trait
   [:span.trait-label (str label ":")]
   [:span.trait-value value]])

(defn character-sheet [character]
  [:div.character-sheet
   [:h2 "Fantasy Character"]
   [:div.character-columns
    [:div.character-column
     [character-trait "Race" (:race character)]
     [character-trait "Class" (:class character)]
     [character-trait "Age" (:age character)]
     [character-trait "Alignment" (:alignment character)]
     [character-trait "Archetype" (:archetype character)]
     [character-trait "Profession" (:profession character)]
     [character-trait "Motivation" (:motivation character)]]
    [:div.character-column
     [character-trait "Hair Color" (:hair-color character)]
     [character-trait "Hair Type" (:hair-type character)]
     [character-trait "Hair Length" (:hair-length character)]
     [character-trait "Hair Feature" (:hair-feature character)]
     [character-trait "Eye Color" (:eye-color character)]
     [character-trait "Proficiency" (:proficiency character)]
     [:div.character-trait
      [:span.trait-label "Personality Traits:"]
      [:div.trait-value (clojure.string/join ", " (:traits character))]]]]])

(defn app []
  (let [embed-mode (:embed-mode @state)]
    [:main {:class (when embed-mode "embed-mode")}
     (when-not embed-mode
       [:header
        [:div.title [:a {:href "../" :style {:color "inherit" :text-decoration "none"}} "Fantasy Generators"]]
        [:nav
         [:a {:href "mailto:chris@mccormick.cx"} "Contact"]]])

     (when-not embed-mode
       [:h1 "Character Generator"])

     [:div.generator-container
      (if-let [character (:character @state)]
        [character-sheet character]
        [:p "Click the button to generate a random fantasy character"])
      [:div.generator-buttons
       [:button
        {:on-click #(swap! state assoc :character (generate-character))}
        "Generate New Character"]
       (when-not embed-mode
         [:button.embed-btn
          {:on-click (fn []
                       (copy-to-clipboard (str (.-origin js/location) (.-pathname js/location) "?embed"))
                       (swap! state assoc :copied true)
                       (js/setTimeout #(swap! state assoc :copied false) 2000))}
          (if (:copied @state) "Copied!" "Copy Embed URL")])]
      (when-not embed-mode
        [:div.attribution "Based on Alyssa Flynn's character tables"])]

     (when-not embed-mode
       [:a.back-link {:href "../index.html"} "← Back to generators"])

     (when-not embed-mode
       [:footer [:a {:href "https://mccormick.cx" :style {:color "#5d1a0f" :text-decoration "none" :font-weight "bold"}} "Made with 🤖 by Chris McCormick"]])
     (when-not embed-mode
       [:div.footer-bg])]))

(swap! state assoc :character (generate-character))
(rdom/render [app] (.getElementById js/document "app"))
