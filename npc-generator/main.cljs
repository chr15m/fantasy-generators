(ns main
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(defonce state (r/atom {:npc nil
                        :rolls nil}))

;; NPC generation data based on the DNDSpeak table
(def race-table
  [{:roll 1 :race "Aasimar"}
   {:roll 2 :race "Dragonborn"}
   {:roll 3 :race "Dwarf"}
   {:roll 4 :race "Elf"}
   {:roll 5 :race "Gnome"}
   {:roll 6 :race "Goblin"}
   {:roll 7 :race "Goliath"}
   {:roll 8 :race "Half-elf"}
   {:roll 9 :race "Half-orc"}
   {:roll 10 :race "Halfling"}
   {:roll 11 :race "Human"}
   {:roll 12 :race "Kenku"}
   {:roll 13 :race "Kobold"}
   {:roll 14 :race "Lizardfolk"}
   {:roll 15 :race "Orc"}
   {:roll 16 :race "Shifter"}
   {:roll 17 :race "Tabaxi"}
   {:roll 18 :race "Tiefling"}
   {:roll 19 :race "Tortle"}
   {:roll 20 :race "Warforged"}])

(def occupation-table
  [{:roll 1 :occupation "Beggar"}
   {:roll 2 :occupation "Shepherd"}
   {:roll 3 :occupation "Undertaker"}
   {:roll 4 :occupation "Actor"}
   {:roll 5 :occupation "Cook"}
   {:roll 6 :occupation "Teacher"}
   {:roll 7 :occupation "Fisherman"}
   {:roll 8 :occupation "Botanist"}
   {:roll 9 :occupation "Bard"}
   {:roll 10 :occupation "Tailor"}
   {:roll 11 :occupation "Miner"}
   {:roll 12 :occupation "Author"}
   {:roll 13 :occupation "Maid/Butler"}
   {:roll 14 :occupation "Blacksmith"}
   {:roll 15 :occupation "Messenger"}
   {:roll 16 :occupation "Gladiator"}
   {:roll 17 :occupation "Mercenary"}
   {:roll 18 :occupation "Alchemist"}
   {:roll 19 :occupation "Innkeeper"}
   {:roll 20 :occupation "King/Queen"}])

(def personality-table
  [{:roll 1 :personality "Flirtatious"}
   {:roll 2 :personality "Curious"}
   {:roll 3 :personality "Nervous"}
   {:roll 4 :personality "Vulgar"}
   {:roll 5 :personality "Gentle"}
   {:roll 6 :personality "Snobbish"}
   {:roll 7 :personality "Generous"}
   {:roll 8 :personality "Pessimistic"}
   {:roll 9 :personality "Secretive"}
   {:roll 10 :personality "Barbaric"}
   {:roll 11 :personality "Friendly"}
   {:roll 12 :personality "Intellectual"}
   {:roll 13 :personality "Humorless"}
   {:roll 14 :personality "Gloomy"}
   {:roll 15 :personality "Careless"}
   {:roll 16 :personality "Honest"}
   {:roll 17 :personality "Proud"}
   {:roll 18 :personality "Optimistic"}
   {:roll 19 :personality "Cruel"}
   {:roll 20 :personality "Cheerful"}])

(def characteristic-table
  [{:roll 1 :characteristic "Muscular"}
   {:roll 2 :characteristic "Lots of tattoos"}
   {:roll 3 :characteristic "Very neat/tidy"}
   {:roll 4 :characteristic "Clumsy"}
   {:roll 5 :characteristic "Very tall"}
   {:roll 6 :characteristic "Piercings"}
   {:roll 7 :characteristic "Fidgets"}
   {:roll 8 :characteristic "Loves storytelling"}
   {:roll 9 :characteristic "Smokes pipe"}
   {:roll 10 :characteristic "Always reading"}
   {:roll 11 :characteristic "Missing Finger"}
   {:roll 12 :characteristic "Extravagant clothing"}
   {:roll 13 :characteristic "Falls asleep instantly"}
   {:roll 14 :characteristic "Lots of jewelery"}
   {:roll 15 :characteristic "Covered in dirt"}
   {:roll 16 :characteristic "Always eating"}
   {:roll 17 :characteristic "Very short"}
   {:roll 18 :characteristic "Always sketching"}
   {:roll 19 :characteristic "Loves gambling"}
   {:roll 20 :characteristic "Attractive"}])

(def speech-table
  [{:roll 1 :speech "High-pitched"}
   {:roll 2 :speech "Excited"}
   {:roll 3 :speech "Always whispers"}
   {:roll 4 :speech "Nasal"}
   {:roll 5 :speech "Gruff"}
   {:roll 6 :speech "Breathy"}
   {:roll 7 :speech "Stutters"}
   {:roll 8 :speech "Fast-talker"}
   {:roll 9 :speech "Tense"}
   {:roll 10 :speech "Thick accent"}
   {:roll 11 :speech "Sing-song voice"}
   {:roll 12 :speech "Dark tone"}
   {:roll 13 :speech "Aggressive"}
   {:roll 14 :speech "Complex vocab"}
   {:roll 15 :speech "Slow, deep voice"}
   {:roll 16 :speech "Lisp"}
   {:roll 17 :speech "Relaxed"}
   {:roll 18 :speech "Booming voice"}
   {:roll 19 :speech "Never tells truth"}
   {:roll 20 :speech "Third person"}])

(defn random-int [max]
  (inc (js/Math.floor (* (js/Math.random) max))))

(defn get-table-result [table roll]
  (let [result (first (filter #(= (:roll %) roll) table))]
    (or result (first table))))

(defn generate-npc []
  (let [race-roll (random-int 20)
        occupation-roll (random-int 20)
        personality-roll (random-int 20)
        characteristic-roll (random-int 20)
        speech-roll (random-int 20)
        
        race (:race (get-table-result race-table race-roll))
        occupation (:occupation (get-table-result occupation-table occupation-roll))
        personality (:personality (get-table-result personality-table personality-roll))
        characteristic (:characteristic (get-table-result characteristic-table characteristic-roll))
        speech (:speech (get-table-result speech-table speech-roll))
        
        rolls {:race race-roll
               :occupation occupation-roll
               :personality personality-roll
               :characteristic characteristic-roll
               :speech speech-roll}]
    
    (swap! state assoc :rolls rolls)
    
    {:race race
     :occupation occupation
     :personality personality
     :characteristic characteristic
     :speech speech}))

(defn generate-description [npc]
  (let [{:keys [race occupation personality characteristic speech]} npc]
    (str "A " personality " " race " " occupation " who " 
         (case characteristic
           "Muscular" "has an impressive physique"
           "Lots of tattoos" "is covered in intricate tattoos"
           "Very neat/tidy" "is impeccably groomed and dressed"
           "Clumsy" "constantly bumps into things"
           "Very tall" "towers over most people"
           "Piercings" "has multiple piercings adorning their face"
           "Fidgets" "can't seem to stay still for a moment"
           "Loves storytelling" "eagerly shares tales with anyone who'll listen"
           "Smokes pipe" "is rarely seen without their smoking pipe"
           "Always reading" "carries a book wherever they go"
           "Missing Finger" "is missing a finger on their right hand"
           "Extravagant clothing" "wears flamboyant, eye-catching attire"
           "Falls asleep instantly" "tends to doze off mid-conversation"
           "Lots of jewelery" "is adorned with numerous pieces of jewelry"
           "Covered in dirt" "appears to have not bathed in weeks"
           "Always eating" "is constantly snacking on something"
           "Very short" "barely reaches waist-height on most people"
           "Always sketching" "is frequently drawing in a small notebook"
           "Loves gambling" "can't resist a bet or game of chance"
           "Attractive" "has striking, memorable features"
           "has a notable physical trait") 
         " and speaks with " 
         (case speech
           "High-pitched" "a surprisingly high-pitched voice"
           "Excited" "constant excitement in their voice"
           "Always whispers" "hushed whispers, even in normal conversation"
           "Nasal" "a distinctly nasal tone"
           "Gruff" "a gruff, gravelly voice"
           "Breathy" "a soft, breathy manner"
           "Stutters" "a noticeable stutter"
           "Fast-talker" "rapid-fire words that tumble out quickly"
           "Tense" "a tense, nervous cadence"
           "Thick accent" "a thick, distinctive accent"
           "Sing-song voice" "a melodic, sing-song pattern"
           "Dark tone" "an ominously dark tone"
           "Aggressive" "aggressive, forceful speech"
           "Complex vocab" "unnecessarily complex vocabulary"
           "Slow, deep voice" "a slow, rumbling deep voice"
           "Lisp" "a pronounced lisp"
           "Relaxed" "a relaxed, unhurried manner"
           "Booming voice" "a booming voice that carries far"
           "Never tells truth" "convincing lies, never the truth"
           "Third person" "odd third-person references to themselves"
           "a distinctive speech pattern") 
         ".")))

(defn npc-trait [label value]
  [:div.npc-trait
   [:span.trait-label (str label ":")]
   [:span.trait-value value]])

(defn npc-sheet [npc]
  [:div.npc-sheet
   [:h2 "Random NPC"]
   
   [npc-trait "Race" (:race npc)]
   [npc-trait "Occupation" (:occupation npc)]
   [npc-trait "Personality" (:personality npc)]
   [npc-trait "Characteristic" (:characteristic npc)]
   [npc-trait "Speech" (:speech npc)]
   
   [:div.npc-description (generate-description npc)]
   
   [:div.attribution "Based on DNDSpeak's Random NPC Generator"]])

(defn app []
  [:main
   [:header
    [:div.title "Fantasy Generator"]
    [:nav
     [:a {:href "#"} "News"]
     [:a {:href "#"} "FAQ"]]]
   
   [:h1 "Random NPC Generator"]
   
   [:div.generator-container
    (if-let [npc (:npc @state)]
      [npc-sheet npc]
      [:div
       [:p "Your players are walking through a city and decide to talk to a random person on the street. Use this generator to create an interesting NPC that would be much more memorable than just a generic peasant!"]
       [:p "Click the button to generate a random NPC."]])
    [:button
     {:on-click #(swap! state assoc :npc (generate-npc))}
     "Generate New NPC"]]
   
   [:a.back-link {:href "../index.html"} "← Back to generators"]
   
   [:footer "© 2025 Fantasy Generator"]])

(swap! state assoc :npc (generate-npc))
(rdom/render [app] (.getElementById js/document "app"))
