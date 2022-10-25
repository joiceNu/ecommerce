(ns ecommerce.aula5
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(def conn (db/abre-conexao))

(db/cria-schema conn)

(db/apaga-banco)



(let [computador (model/novo-produto "Computador Novo"  "/computador-novo ", 2500M)
      celular (model/novo-produto "Celular Caro", "/celular", 8888888.10M)]
  (d/transact conn [computador, celular]))

(def fotografia-no-passado (d/db conn))

(let [calculadora {:produto/nome "Calculadora com 4 operações"}
      celular-barato (model/novo-produto "Celular Barato" "/celular-barato" 0.1M )]
    (d/transact conn [calculadora, celular-barato]))

(pprint (db/todos-os-produtos (d/db conn)))

(pprint (db/todos-os-produtos fotografia-no-passado))

; rodando a query num banco filtrado com dados do passado
(pprint (db/todos-os-produtos
          (d/as-of (d/db conn) #inst"2022-10-25T14:58:58.806-00:00" )))