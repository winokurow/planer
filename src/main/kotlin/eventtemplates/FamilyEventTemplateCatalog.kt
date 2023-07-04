package eventtemplates

import models.Category
import models.EventTemplate
import models.WeightObject
import java.time.Duration

class FamilyEventTemplateCatalog : EventTemplateCatalog {

    override fun getEventTemplate(maxDuration: Duration): EventTemplate? {
        val eventDuration = Duration.parse("PT60M")
        if (eventDuration <= maxDuration) {
            val weightsList: MutableList<WeightObject> = mutableListOf()
            weightsList.add(WeightObject(Category.FAMILY, 60))
            return EventTemplate("Семья", 0, eventDuration, weightsList, "")
        }

        return null
    }

}