(ns c2s-proxy-service.socket-channel
  (:import (java.nio.channels ClosedChannelException)))

;; TODO Wrapper not complete. Lacking open, connect, read and write.

(defn- input-shutdown?
  [socket]
  (-> (.socket socket)
      (.isInputShutdown)))

(defn- output-shutdown?
  [socket]
  (-> (.socket socket)
      (.isOutputShutdown)))

(defn- remote-address
  [socket]
  (host-address (try (.getRemoteAddress socket)
                     (catch ClosedChannelException _ nil))))

(defn- remote-port
  [socket]
  (port (try (.getRemoteAddress socket)
             (catch ClosedChannelException _ nil))))

(defn- connected?
  [socket]
  (.isConnected socket))

(defn- connection-pending?
  [socket]
  (.isConnectionPending socket))

(defn socket-info
  [socket]
  (conj (server-socket-info socket)
        {:remote-address (remote-address socket)
         :remote-port (remote-port socket)
         :connected? (connected? socket)
         :connection-pending? (connection-pending? socket)
         :input-shutdown? (input-shutdown? socket)
         :output-shutdown? (output-shutdown? socket)}))

(defn shutdown-input!
  [socket]
  (.shutdownInput socket))

(defn shutdown-output!
  [socket]
  (.shutdownOutput socket))
