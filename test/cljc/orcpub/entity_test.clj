(ns orcpub.entity-test
  (:require [clojure.test :refer :all]
            [clojure.spec :as spec]
            [clojure.spec.test :as stest]
            [orcpub.entity.strict :as e]
            [orcpub.entity :as entity]
            [orcpub.entity-spec :as es]
            [orcpub.template :as t]
            [orcpub.dnd.e5.modifiers :as modifiers]
            [orcpub.dnd.e5.character :as char5e]
            [orcpub.dnd.e5.character.equipment :as equip]))

(def character {::entity/options
                {:race
                 {::entity/key :elf,
                  ::entity/options {:subrace {:orcpub.entity/key :wood-elf}}}
                 :weapons
                 [{::entity/key :javelin,
                   ::entity/value
                   {::equip/quantity 4,
                    ::equip/equipped? true,
                    ::equip/class-starting-equipment? true}}]}
                ::entity/values
                {::char5e/eyes "green"
                 ::char5e/custom-equipment [{::equip/name "Scroll of Pedigree"
                                     ::equip/equipped? true
                                             ::equip/background-starting-equipment? true}]}})

(def strict-character {::e/selections [{::e/key :race
                                        ::e/option {::e/key :elf
                                                    ::e/selections [{::e/key :subrace
                                                                     ::e/option {::e/key :wood-elf}}]}}
                                       {::e/key :weapons
                                        ::e/options [{::e/key :javelin
                                                      ::e/map-value {::equip/quantity 4
                                                                     ::equip/equipped? true
                                                                     ::equip/class-starting-equipment? true}}]}]
                       ::e/values {::char5e/eyes "green"
                                   ::char5e/custom-equipment [{::equip/name "Scroll of Pedigree"
                                                               ::equip/equipped? true
                                                               ::equip/background-starting-equipment? true}]}})

(deftest test-to-strict
  (stest/instrument `entity/to-strict)
  (is (= (entity/to-strict character) strict-character))
  (stest/unstrument `entity/to-strict))

(deftest test-from-strict
  (stest/instrument `entity/from-strict)
  (is (= (entity/from-strict strict-character) character))
  (stest/unstrument `entity/from-strict))

(deftest test-template-option-map
  (let [selections [(assoc
                     (t/selection-cfg
                      {:name "Selection X"
                       :options [(t/option-cfg
                                  {:name "Option 1"})
                                 (t/option-cfg
                                  {:name "Option 2"})]})
                     ::entity/path
                     [:selection-x])
                    (assoc
                     (t/selection-cfg
                      {:name "Selection Y"
                       :options [(t/option-cfg
                                  {:name "Option 3"})
                                 (t/option-cfg
                                  {:name "Option 4"})]})
                     ::entity/path
                     [:selection-y])]
        expected {[:selection-x :option-1] (t/option-cfg
                                            {:name "Option 1"})
                  [:selection-x :option-2] (t/option-cfg
                                            {:name "Option 2"})
                  [:selection-y :option-3] (t/option-cfg
                                            {:name "Option 3"})
                  [:selection-y :option-4] (t/option-cfg
                                            {:name "Option 4"})}]
    (is (= (entity/make-template-option-map selections) expected))))
