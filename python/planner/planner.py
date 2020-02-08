from datetime import timedelta

import math


class Planner:

    def __init__(self, employees, settings):
        self.employees = employees
        self.settings = settings

    def plan_month(self, date_from, date_to, preferences):
        plan = {}

        available_shifts = []

        for empl in self.employees:
            shift_count = math.ceil(self.settings.max_month_hours * empl.workload / self.settings.shift_length)
            for i in range(shift_count):
                available_shifts.append([i, empl])

        days = date_to - date_from
        for day in range(days.days):
            current_day = date_from + timedelta(days=day)
            plan[current_day] = []
            for j in range(0, 4):
                empl_shift = available_shifts.pop()
                plan[current_day].append(empl_shift[1])

        return plan

    def validate_plan(self, plan):

        for day, empls in plan.items():
            for empl in empls:

