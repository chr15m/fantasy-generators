(ns main
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(defonce state (r/atom {:selected nil}))

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
    ;[card "DUNGEON" "https://placehold.co/300x300/e0e0e0/333?text=Dungeon" "#"]
    ]
   
   [:footer [:a {:href "https://mccormick.cx" :style {:color "#5d1a0f" :text-decoration "none" :font-weight "bold"}} "Made with 🤖 by Chris McCormick"]]
   [:div.footer-bg]])

(rdom/render [app] (.getElementById js/document "app"))
