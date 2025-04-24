(ns main
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(defonce state (r/atom {:selected nil
                        :copied false}))

(defn copy-to-clipboard [text]
  (let [el (.createElement js/document "textarea")]
    (set! (.-value el) text)
    (.appendChild (.-body js/document) el)
    (.select el)
    (.execCommand js/document "copy")
    (.removeChild (.-body js/document) el)))

(defn get-embed-url [path]
  (str (.-origin js/location) (.-pathname js/location) path "?embed"))

(defn card [title image-url link]
  [:div.card
   {:on-click #(js/window.location.assign link)
    :class (when (= title (:selected @state)) "selected")}
   [:div.card-image [:img {:src image-url :alt title}]]
   [:div.card-title title]])

(defn app []
  [:main
   [:header
    [:div.title [:a {:href "./" :style {:color "inherit" :text-decoration "none"}} "Fantasy Generators"]]
    [:nav
     [:a {:href "mailto:chris@mccormick.cx"} "Contact"]]]

   [:h1 "Pick a generator..."]

   [:div.cards-container
    [card "TAVERN NAME" "./tavern-name-generator/card.jpg" "./tavern-name-generator/"]
    [card "CHARACTER" "./character-generator/card.jpg" "./character-generator/"]
    [card "NPC" "./npc-generator/card.jpg" "./npc-generator/"]
    [card "DUNGEON ROOM" "./dungeon-room-generator/card.jpg" "./dungeon-room-generator/"]
    ]

   [:footer [:a {:href "https://mccormick.cx" :style {:color "#5d1a0f" :text-decoration "none" :font-weight "bold"}} "Made with 🤖 by Chris McCormick"]]
   [:div.footer-bg]])

(rdom/render [app] (.getElementById js/document "app"))
