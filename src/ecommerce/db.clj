(ns ecommerce.db
  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn abre-conexao []
  (d/create-database db-uri)
  (d/connect db-uri)
  )

(defn apaga-banco []
  (d/delete-database db-uri)
  )



(defn todos-os-produtos-com-slug [db slug]
  (d/q '[:find ?entidade
        :where [?entidade :produto/slug ?slug]]
  db slug))

;;busca apenas no grupo do in
(defn todos-os-produtos-por-slug [db slug]
 (d/q '[:find ?entidade
         :in $ ?slug-a-ser-buscado
  :where [?entidade :produto/slug ?slug-a-ser-buscado]] db slug))


(def schema
  [{:db/ident :produto/nome
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "O nome de um produto"
    }
   {:db/ident :produto/slug
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "O caminho para acessar esse produto via http"
    }
   {:db/ident :produto/preco
    :db/valueType :db.type/bigdec
    :db/cardinality :db.cardinality/one
    :db/doc "O preço do produto com precisao monetária"
    }
   {:db/ident :produto/palavra-chave
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/many}
   ])

(defn cria-schema [conn]
  (d/transact conn schema)
  )

;entity => ?entidade => ?produto => ?p
(defn todos-os-slugs [db]
  (d/q '[:find ?slug
         :where [_ :produto/slug ?slug]] db))

(defn todos-os-produtos-por-preco [db]
  (d/q '[:find ?nome, ?preco
         :where [?e :produto/preco ?preco]
                 [?e produto/nome ?nome]] db))

;;maneiras de retornar um mapa
;;usando keys para cada chave do find
(defn todos-os-produtos-por-preco [db]
  (d/q '[:find ?nome, ?preco
         :keys nome, preco
         :where [?produto :produto/preco ?preco]
         [?produto :produto/nome ?nome]] db))


;;Exemplo do retorno
;; #:produto{:nome "Computador Novo", :preco 2500.10M}
(defn todos-os-produtos-por-preco [db]
  (d/q '[:find ?nome, ?preco
         :keys produto/nome, produto/preco
         :where [?produto :produto/preco ?preco]
         [?produto :produto/nome ?nome]] db))

;;passar um array com as chaves que queremos da entidade
(defn todos-os-produtos [db]
  (d/q '[:find (pull ?entidade [:produto/nome :produto/preco :produto/slug])
         :where [?entidade :produto/nome _]] db))

;;traz tudo inclusive o id da entidade
(defn todos-os-produtos [db]
  (d/q '[:find (pull ?entidade [*])
         :where [?entidade :produto/nome]] db))

(defn todos-os-produtos-por-preco [db preco-minimo-requisitado]
  (d/q '[:find ?nome, ?preco
         :in $, ?preco-minimo
         :keys produto/nome, produto/preco
         :where [?produto :produto/preco ?preco]
         [(> ?preco ?preco-minimo)]
         [?produto :produto/nome ?nome]
         ]

       db preco-minimo-requisitado))



(defn todos-os-produtos-por-palavra-chave[db palavra-chave-buscada]
  (d/q '[:find(pull ?produto[*])
         :in $ ?palavra-chave
         :where [?produto :produto/palavra-chave ?palavra-chave]]
       db palavra-chave-buscada))

; se temos 10 mil...se temos mil produtos com preco > 5000 e so 10 produtos com quantidade < 10
;;[(> preco 5000)]        ; => 5000 datom
;;[(< quantidade 10)]     ; => 10 datom

; passar por 10 mil
;;[(< quantidade 10)]     ; => 10 datom
;;[(> preco 5000)]        ; => 10 datom
;
; em geral vamos deixar as condicoes da mais restritiva para a menos restritiva pois o plano de acao somos nos quem tomamos

