package eventtemplates

import models.EventTemplate
import java.time.Duration

interface EventTemplateCatalog {
    fun getEventTemplate(maxDuration: Duration): EventTemplate?
}