(ns ecommerce.aula6
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(def conn (db/abre-conexao))

(db/cria-schema conn)

(db/apaga-banco)

(let [computador (model/novo-produto "Computador Novo," ,"/computador-novo"', 2500M)
      celular (model/novo-produto "Celular Caro", "/celular", 8888888.10M)
      calculadora { :produto/nome "Calculadora com 4 operações"}
      celular-barato (model/novo-produto "Celular Barato"  "/celular-barato" 0.1M)]
      (d/transact conn [calculadora, celular-barato computador celular])
  )

(db/todos-os-produtos-por-preco (d/db conn) 1000)

; tem que trazer 1

(db/todos-os-produtos-por-preco (d/db conn) 5000)

; (db/apaga-banco)
(d/transact conn [[:db/add 17592186045418 :produto/palavra-chave "desktop"]
                  [:db/add 17592186045418 :produto/palavra-chave "computador"]])

;;tirar uma chave
(d/transact conn [[:db/retract 17592186045418 :produto/palavra-chave "computador"]])

(pprint (db/todos-os-produtos(d/db conn)))


(d/transact conn [[:db/add 17592186045419 :produto/palavra-chave "celular"]])
(d/transact conn [[:db/add 17592186045421 :produto/palavra-chave "celular"]])

(pprint (db/todos-os-produtos(d/db conn)))

(pprint (db/todos-os-produtos-por-palavra-chave(d/db conn) "celular"))

(pprint (db/todos-os-produtos(d/db conn)))

;(db/apaga-banco)

;(db/apaga-banco)

;(db/apaga-banco)