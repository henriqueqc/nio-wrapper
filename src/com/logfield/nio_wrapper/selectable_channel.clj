(ns com.logfield.nio-wrapper.selectable-channel
  (:require [c2s-proxy-service.nio-wrapper.operation-set :as operation-set]))

(defn- valid-operations
  [selectable-channel]
  (-> selectable-channel
      .validOps
      operation-set/int->operations))

(defn selectable-channel-info
  [selectable-channel]
  {:selectable-channel selectable-channel
   :blocking-lock (.blockingLock selectable-channel)
   :blocking? (.isBlocking selectable-channel)
   :open? (.isOpen selectable-channel)
   :provider (.provider selectable-channel)
   :registered? (.isRegistered selectable-channel)
   :valid-operations (valid-operations selectable-channel)})

(defn selection-key
  [selectable-channel selector]
  (.keyFor selectable-channel selector))

(defn close!
  [selectable-channel]
  (.close selectable-channel))

(defn configure-blocking!
  [selectable-channel blocking]
  (.configureBlocking selectable-channel blocking))

(defn register!
  ([selectable-channel {:keys [selector operations attachment]
                        :or [attachment nil]}]
   (.register selectable-channel
              selector
              (operation-set/operations->int operations)
              attachment)))
