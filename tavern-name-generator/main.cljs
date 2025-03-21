(ns main
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]))

;; Original adjectives and nouns from the HTML version
(def adjectives
  ["Drunken" "Prancing" "Jolly" "Sleeping" "Laughing" "Dancing"
   "Thirsty" "Rusty" "Golden" "Silver" "Copper" "Iron" "Brass"
   "Broken" "Wandering" "Howling" "Whispering" "Roaring" "Silent"])

(def nouns
  ["Dragon" "Unicorn" "Goblin" "Elf" "Dwarf" "Knight" "Wizard"
   "Tankard" "Flagon" "Barrel" "Sword" "Shield" "Axe" "Hammer"
   "Pony" "Stag" "Wolf" "Lion" "Eagle" "Raven" "Crow" "Fox"])

;; Additional words from the ClojureScript version
(def first-words
  {1 "The" 2 "The" 3 "The" 4 "Ye Olde"})

(def second-words
  {1 "Dancing" 2 "Leaky" 3 "Shrieking" 4 "Chivalrous" 5 "Lonesome"
   6 "Red" 7 "Hideous" 8 "Saucy" 9 "Jolly" 10 "Grinning"
   11 "Fancy" 12 "Drunken" 13 "Gilded" 14 "Blue" 15 "Blushing"
   16 "Magic" 17 "Regal" 18 "Silver" 19 "Crying" 20 "Bloody"})

(def third-words
  {1 "Maiden" 2 "Dragon" 3 "Sword" 4 "Blade" 5 "Stein"
   6 "Wizard" 7 "Crow" 8 "Serpent" 9 "Mirror" 10 "Wyrm"
   11 "Boar" 12 "Crown" 13 "Prince" 14 "Archer" 15 "Flute"
   16 "Casket" 17 "Sailor" 18 "Thief" 19 "Moon" 20 "Broom"})

(def fourth-words
  {1 "" 2 "" 3 "" 4 "" 5 "Tavern" 6 "Bar" 7 "Alehouse" 8 "Pub"})

(defn random-int [max]
  (inc (js/Math.floor (* (js/Math.random) max))))

(defn generate-tavern-name-complex []
  (let [first-roll (random-int 4)
        second-roll (random-int 20)
        third-roll (random-int 20)
        fourth-roll (random-int 8)
        first-word (get first-words first-roll)
        second-word (get second-words second-roll)
        third-word (get third-words third-roll)
        fourth-word (get fourth-words fourth-roll)
        name-parts (filter not-empty [first-word second-word third-word fourth-word])]
    (clojure.string/join " " name-parts)))

(defn generate-tavern-name-simple []
  (let [adjective (nth adjectives (js/Math.floor (* (js/Math.random) (count adjectives))))
        noun (nth nouns (js/Math.floor (* (js/Math.random) (count nouns))))]
    (str "The " adjective " " noun)))

(defn generate-tavern-name []
  (if (< (js/Math.random) 0.5)
    (generate-tavern-name-simple)
    (generate-tavern-name-complex)))

;; Initialize with a random tavern name
(defonce state (r/atom {:tavern-name (generate-tavern-name)
                        :embed-mode (boolean (re-find #"[?&]embed" (.-search js/location)))}))

(defn copy-to-clipboard [text]
  (let [el (.createElement js/document "textarea")]
    (set! (.-value el) text)
    (.appendChild (.-body js/document) el)
    (.select el)
    (.execCommand js/document "copy")
    (.removeChild (.-body js/document) el)))

(defn app []
  (let [embed-mode (:embed-mode @state)]
    [:main {:class (when embed-mode "embed-mode")}
     (when-not embed-mode
       [:header
        [:div.title [:a {:href "../" :style {:color "inherit" :text-decoration "none"}} "Fantasy Generators"]]
        [:nav
         [:a {:href "mailto:chris@mccormick.cx"} "Contact"]]])

     (when-not embed-mode
       [:h1 "Tavern Name Generator"])

     [:div.generator-container
      (when-not embed-mode
        [:p "Your random tavern name is:"])
      [:div.tavern-name
       (if-let [name (:tavern-name @state)]
         name
         "Click the button to generate a tavern name")]
      [:div.generator-buttons
       [:button
        {:on-click #(swap! state assoc :tavern-name (generate-tavern-name))}
        "Generate New Name"]]
      (when-not embed-mode
        [:div.attribution "Based on Basalt's Tavern Name Generator"])
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

(rdom/render [app] (.getElementById js/document "app"))
