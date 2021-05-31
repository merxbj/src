from lxml import etree
from xmldiff import main, formatting

diff = main.diff_files(r'c:\src\PCS_RPOS_Version_6\Releases\06_10_C3\6.1\RSM\TextLiterals\LocalizationTools\French\SimplifiedLiterals-Sorted.xml',
                       r'c:\src\PCS_RPOS_Version_6\Releases\06_10_C3\6.1\RSM\TextLiterals\LocalizationTools\French\SimplifiedLiterals.fr-BA-Sorted.xml',
                       formatter=formatting.XMLFormatter())

print(diff)
