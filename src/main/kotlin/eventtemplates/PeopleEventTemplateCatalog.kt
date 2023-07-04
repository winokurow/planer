package eventtemplates

import models.Category
import models.EventTemplate
import models.WeightObject
import java.time.Duration

class PeopleEventTemplateCatalog : EventTemplateCatalog {

    override fun getEventTemplate(maxDuration: Duration): EventTemplate? {
        val eventDuration = Duration.parse("PT180M")
        if (eventDuration <= maxDuration) {
            val weightsList: MutableList<WeightObject> = mutableListOf()
            weightsList.add(WeightObject(Category.PEOPLE, 180))
            return EventTemplate("Общение", 0, eventDuration, weightsList, "")
        }

        return null
    }

}