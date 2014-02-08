(ns login_sample.handler
  (:use compojure.core
        hiccup.core
        hiccup.form
        [ring.middleware.session.memory :only (memory-store)]
        [noir.cookies :only (wrap-noir-cookies)]
        [noir.session :only (mem wrap-noir-session wrap-noir-flash)]
        [noir.util.middleware :only (app-handler)])
  (:require [noir.response :as resp]
            [noir.session :as session]
            [compojure.handler :as handler]))


(defn main-layout [& content]
  (html
   [:body
    [:div#wrapper
     [:div.content
      content]]]))


(defroutes main
  (GET "/" []
       (main-layout
        [:h1 "Hello noir!"]
        (if (session/get :username)
          [:div
           [:h1 "Hello " (session/get :username)]
           [:form {:method "post" :action "/logout"}
            [:input {:type "submit" :value "Logout"}]]]
          [:form {:method "post"}
           [:input {:type "text" :placeholder "username" :name "username" :id "username"}]
           [:input {:type "password" :placeholder "password" :name "password" :id "password"}]
           [:input {:type "submit" :value "Login"}]
           ])))

  (POST "/" [username password]
        (session/put! :username username)
        (resp/redirect "/"))

  (POST "/logout" []
        (session/remove! :username)
        (resp/redirect "/")))



(def app
  (app-handler
      [main]
      :session-options {:cookie-name "loginapp"
                        :max-age 1000
                        :store (memory-store)}))
