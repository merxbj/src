from dbfread import DBF
import csv
from xml.dom import minidom
from xml.dom.minidom import getDOMImplementation

doc_name = 'PSRK'

root_element_name = {'PSRK': 'PS_REGKAND', 'PSRKL': 'PS_RKL'}
row_element_name = {'PSRK': 'PS_REGKAND_ROW', 'PSRKL': 'PS_RKL_ROW'}

impl = getDOMImplementation()
xml = impl.createDocument(None, root_element_name[doc_name], None)
xml_root = xml.documentElement

table = DBF('/users/jaroslavlek/Downloads/PS2006reg2006/{}.dbf'.format(doc_name), encoding='cp852')

for record in table:
    row_element = xml_root.appendChild(xml.createElement(row_element_name[doc_name]))
    for value, field_name in zip(record.values(), table.field_names):
        value_element = row_element.appendChild(xml.createElement(field_name))
        value_element.appendChild(xml.createTextNode(str(value)))

with open('/users/jaroslavlek/Downloads/PS2006reg2006/{}.xml'.format(doc_name), mode='w') as f:
    f.write(xml.toxml())