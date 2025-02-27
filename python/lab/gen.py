from python_gedcom_2.element.individual import IndividualElement
from python_gedcom_2.parser import Parser

# Path to your ".ged" file
file_path = r'c:\Users\jm185267\Downloads\ye41co_8067680j1q3ce86rk45cey_A.ged'

# Initialize the parser
gedcom_parser = Parser()

# Parse your file
gedcom_parser.parse_file(file_path)

root_child_elements = gedcom_parser.get_root_child_elements()

# Iterate through all root child elements
for element in root_child_elements:

    # Is the "element" an actual "IndividualElement"? (Allows usage of extra functions such as "surname_match" and "get_name".)
    if isinstance(element, IndividualElement):

        # Get all individuals whose surname matches "Doe"
        if element.surname_match('Doe'):

            # Unpack the name tuple
            (first, last) = element.get_name()

            # Print the first and last name of the found individual
            print(first + " " + last)