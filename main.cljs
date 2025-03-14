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
    [:div.title "Fantasy Generator"]
    [:nav
     [:a {:href "#"} "News"]
     [:a {:href "#"} "FAQ"]]]
   
   [:h1 "Pick a generator..."]
   
   [:div.cards-container
    [card "TAVERN NAME" "./tavern-name-generator/card.png" "./tavern-name-generator/"]
    [card "CHARACTER" "./character-generator/card.png" "./character-generator/"]
    [card "NPC" "./npc-generator/card.png" "./npc-generator/"]
    ;[card "DUNGEON" "https://placehold.co/300x300/e0e0e0/333?text=Dungeon" "#"]
    ]
   
   [:footer "© 2025 Fantasy Generator"]])

(rdom/render [app] (.getElementById js/document "app"))
