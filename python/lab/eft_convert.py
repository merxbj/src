import io, os
import re

with io.open(r"C:\temp\okq8\eft\mapping.csv", "r", encoding="UTF-8") as f:
    mapping_lines = f.readlines()

with io.open(r"c:\temp\okq8\eft\current.sql", "r", encoding="UTF-8") as f:
    current_sql_lines = f.readlines()

mappings = []
for mapping_line in mapping_lines:
    mappings.append(tuple([ x.strip() for x in mapping_line.split(";") if x.strip() ]))

current_efts = []
for current_sql_line in current_sql_lines:
    match = re.match(r"^(EXEC p_Add_Credit_Category)\s+\'(.+)\'\s*\,\s*\'(.+)\'\s*\,\s*(\d+)$", current_sql_line)
    current_efts.append(match.groups())

new_eft_sqls = {}

for current_eft in current_efts:
    (exec_sql, customer, current_name, eft) = current_eft
    print("-- Converting {}(eft1: {}):".format(current_name, eft))

    if eft not in new_eft_sqls:
        new_eft_sqls[eft] = []
    else:
        print("\t-- WARNING: DUPLICATED EFT Definition!")

    for mapping in mappings:
        (eft2, eft1, old, new_name) = mapping
        if eft == eft1 + old:
            (name_prefix, name_eft, name_name) = tuple([ component.strip() for component in current_name.split(" - ") if component.strip() ])
            new_eft =  eft2 + eft1 + old
            new_full_name = "{} - {} - {}".format(name_prefix, new_eft, new_name)
            sql = "{} '{}', '{}', {}".format(exec_sql, customer, new_full_name, new_eft)
            new_eft_sqls[eft].append((new_eft, sql))
            print("\t-- to: {}(eft2: {})".format(new_full_name, new_eft))

    if len(new_eft_sqls[eft]) == 0:
        print("\t-- to: WARNING: NO MAPPING FOUND!")
        del new_eft_sqls[eft]

print("\nBEGIN TRANSACTION\n")

for eft, sqls in new_eft_sqls.items():
    for sql in sqls:
        print(sql[1])

print("\nCOMMIT")

# BEING TRANSACTION + ROLLBACK
sql_merge_template = """
MERGE INTO EPS_Card_Restriction_Group_Category_List AS Target
USING
(
    SELECT ECRGCL.eps_card_restriction_group_id, EFT2.credit_category_code, GETDATE() AS last_modified_timestamp
    FROM EPS_Card_Restriction_Group_Category_List AS ECRGCL
    CROSS JOIN
    (
        {}
    ) AS EFT2
    WHERE ECRGCL.credit_category_code = {}
) AS Source
ON (Target.eps_card_restriction_group_id = Source.eps_card_restriction_group_id) AND (Target.credit_category_code = Source.credit_category_code)
WHEN NOT MATCHED BY TARGET THEN
    INSERT (eps_card_restriction_group_id, credit_category_code, last_modified_timestamp) VALUES (Source.eps_card_restriction_group_id, Source.credit_category_code, Source.last_modified_timestamp);
"""

sql_select_eft2_template = "          SELECT {} as credit_category_code"
sql_union_select_eft2_template = "        UNION ALL SELECT {} as credit_category_code"

print("\n\nBEGIN TRANSACTION")

for eft, sqls in new_eft_sqls.items():
    sql_select_eft2 = ""
    for sql in sqls:
        if len(sql_select_eft2) == 0:
            sql_select_eft2 = sql_select_eft2_template.format(sql[0])
        else:
            sql_select_eft2 += "\n" + sql_union_select_eft2_template.format(sql[0])
    if len(sql_select_eft2) > 0:
        sql_merge = sql_merge_template.format(sql_select_eft2, eft)
        print(sql_merge)

print("COMMIT")