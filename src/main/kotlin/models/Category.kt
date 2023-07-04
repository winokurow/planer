package models

import eventtemplates.*

enum class Category(val coefficient: Double) {
    LEISURE(0.5) {
        override fun createImplementation(): EventTemplateCatalog {
            return LeisureEventTemplateCatalog()
        }
    },
    NECESSARY(0.1) {
        override fun createImplementation(): EventTemplateCatalog {
            return NecessaryEventTemplateCatalog()
        }
    },
    SPORT(0.1) {
        override fun createImplementation(): EventTemplateCatalog {
            return SportEventTemplateCatalog()
        }
    },
    STUDY(0.05) {
        override fun createImplementation(): EventTemplateCatalog {
            return StudyEventTemplateCatalog()
        }
    },
    DIFFERENT(0.1) {
        override fun createImplementation(): EventTemplateCatalog {
            return DifferentEventTemplateCatalog()
        }
    },
    HYGIENE(0.02) {
        override fun createImplementation(): EventTemplateCatalog {
            return HygieneEventTemplateCatalog()
        }
    },
    CLEAN(0.08) {
        override fun createImplementation(): EventTemplateCatalog {
            return CleanEventTemplateCatalog()
        }
    },
    FAMILY(0.02) {
        override fun createImplementation(): EventTemplateCatalog {
            return FamilyEventTemplateCatalog()
        }
    },
    PEOPLE(0.03) {
        override fun createImplementation(): EventTemplateCatalog {
            return LeisureEventTemplateCatalog()
        }
    };

    abstract fun createImplementation(): EventTemplateCatalog
}