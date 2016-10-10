(ns com.logfield.nio-wrapper.server-socket-channel
  (:require [com.logfield.nio-wrapper.selectable-channel :as selectable-channel])
  (:import (java.net InetSocketAddress)
           (java.nio.channels ServerSocketChannel ClosedChannelException)))

(defn- bound?
  [socket]
  ;; Check if open because if the socket was bound and closed manualy after,
  ;; the java isBound method wrongly returns isBound as true is this case.
  (if (.isOpen socket)
    (-> (.socket socket)
        (.isBound))
    false))

(defn- supported-options
  [socket]
  (letfn [(SocketOption->map
            [socket-option]
            (hash-map (keyword (.name socket-option))
                      socket-option))]
    (->> (map SocketOption->map (.supportedOptions socket))
         (into {}))))

(defn- option-value
  [socket option-name]
  (try
    (->> ((supported-options socket) option-name)
         (.getOption socket))
    ;; Sometimes the option is listed as supported, but when you call getOption
    ;; it thows an AssertionError "Option not found", so the value is nil in
    ;; that case.
    (catch AssertionError _ nil)
    (catch ClosedChannelException _ nil)))

(defn- options
  [socket]
  (letfn [(option-hash-map
            [option-name]
            (hash-map option-name (option-value socket option-name)))]
    (->> (supported-options socket)
         (keys)
         (map option-hash-map)
         (into {}))))

(defn- host-address
  [socket-address]
  (try (-> (.getAddress socket-address)
           (.getHostAddress))
       (catch NullPointerException exception nil)))

(defn- port
  [socket-address]
  (try (.getPort socket-address)
       (catch NullPointerException exception nil)))

(defn- local-address
  [socket]
  (host-address (try (.getLocalAddress socket)
                     (catch ClosedChannelException _ nil))))

(defn- local-port
  [socket]
  (port (try (.getLocalAddress socket)
             (catch ClosedChannelException _ nil))))

(defn server-socket-channel-info
  [socket]
  (conj (selectable-channel/selectable-channel-info socket)
        {:server-socket socket
         :local-address (local-address socket)
         :local-port (local-port socket)
         :options (options socket)
         :open? (.isOpen socket)
         :bound? (bound? socket)}))

(defn server-socket-channel!
  []
  (ServerSocketChannel/open))

(defn accept!
  [socket]
  (.accept socket))

(defn bind!
  [socket {:keys [hostname port backlog]
           :or {hostname "0.0.0.0"
                port 0
                backlog 0}}]
  ;; TODO: Do not let getByName resolve the hostname, this is a blocking
  ;; operation. The InetSocketAdress constructor uses the
  ;; InetAddress.getByName(hostname) class method to resolve the hostname into
  ;; an InetAddress. See the getByName documentation.
  (.bind socket (InetSocketAddress. hostname port) backlog))

(defn set-option!
  [socket option-name value]
  (let [value (if (= (type value) Long)
                (int value)
                value)]
    (.setOption socket ((supported-options socket) option-name) value)))

(defn close!
  [socket]
  (.close socket))
