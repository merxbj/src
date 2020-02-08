from datetime import date

from planner import Planner


class EmployeeType:
    Nurse = "Nurse"
    CareTaker = "CareTaker"


class Employee:
    def __init__(self, id, first_name, last_name):
        self.id = id
        self.first_name = first_name
        self.last_name = last_name
        self.workload = 1.0
        self.experience = 1.0
        self.type = EmployeeType.Nurse

    def __str__(self):
        return self.first_name + " " + self.last_name + " "


class Settings:
    def __init__(self):
        self.work_day_hours = 7.5
        self.max_over_time = 20
        self.min_nurses = 3.0
        self.max_nurses = 4.0
        self.shift_length = 11.5


class Preferences:
    pass


class ConsolePrinter:
    def print_plan(self, plan):
        print("Work Plan Printout:")
        print()
        for day, empls in plan.items():
            print(str(day) + ":")
            for empl in empls:
                print("\t" + str(empl))
            print()


def get_employees():
    employees = [Employee(1, "Lenka", "Merxbauerova"),
                 Employee(2, "Elenka", "Merxbauerova"),
                 Employee(3, "Martin", "Merxbauer"),
                 Employee(4, "Jaroslav", "Merxbauer")]

    for i in range(len(employees) + 1, 20 + 1):
        employees.append(Employee(i, "First" + str(i), "Last" + str(i)))

    print("Getting the following employees:")
    for emp in employees:
        print("\t" + str(emp))

    return employees


def get_settings():
    return Settings()


def get_preferences():
    return Preferences()


def get_date_from():
    return date(day=1, month=8, year=2018)


def get_date_to():
    return date(day=31, month=8, year=2018)


def main():
    employees = get_employees()
    settings = get_settings()
    preferences = get_preferences()
    date_from = get_date_from()
    date_to = get_date_to()

    planner = Planner(employees, settings)
    plan = planner.plan_month(date_from, date_to, preferences)

    printer = ConsolePrinter()
    printer.print_plan(plan)


if __name__ == "__main__":
    main()
