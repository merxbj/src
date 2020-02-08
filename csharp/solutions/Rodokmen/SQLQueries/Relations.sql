select	Person.first_name as 'Osoba.Jmeno',
		Person.last_name as 'Osoba.Prijmeni',
		RelatedPerson.first_name as 'Pribuzny.Jmeno',
		RelatedPerson.last_name as 'Pribuzny.Prijmeni',
		case RelatedPerson.gender
			when 'M' then D.male_name
			when 'F' then D.female_name
		end as 'Vztah'

from Relations as R

join d_relations as D
	on R.relation_id = D.relation_id

join Persons as Person
	on R.person_id = Person.person_id

join Persons as RelatedPerson
	on R.related_person_id = RelatedPerson.person_id