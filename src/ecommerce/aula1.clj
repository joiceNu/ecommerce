(ns ecommerce.aula1
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))






(d/transact conn db/schema)
(let [computador
      (model/novo-produto "Computador Novo" "/computador_novo" 2500.10M)]
  (d/transact conn [computador])
  )

(let [celular
      (model/novo-produto "Celular Caro" "/celular" 2500.10M)]
  (d/transact conn [celular])
  )

(def db (d/db conn))

(d/q '[:find ?entidade
       :where [?entidade :produto/nome]] db)

(let [calculadora
      {:produto/nome "Calculadora com 4 operações"}]
  (d/transact conn [calculadora])
  )


(let [celular-barato (model/novo-produto "Celular Barato", "/celular-barato", 8888.10M)
      resultado @(d/transact conn [celular-barato])
      id-entidade (-> resultado :tempids vals first)]
  (pprint resultado)
  (pprint @(d/transact conn [[:db/add id-entidade :produto/preco 0.1M]]))
  (pprint @(d/transact conn [[:db/retract id-entidade :produto/slug "/celular-barato"]])))
[[[]]]

(let [celular
      (model/novo-produto "Celular Barato" "/celular-barato" 2500.10M)]
  (d/transact conn [celular])
  )

(db/apaga-banco)