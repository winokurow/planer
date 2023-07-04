package models

import Event

class Day(val dayOfWeek: DayOfWeek, val events: List<Event>) {
}