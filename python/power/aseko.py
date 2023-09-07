from aiohttp import ClientSession
from asyncio import run

import aioaseko


async def main():
    async with ClientSession() as session:
        account = aioaseko.MobileAccount(session, "user", "password")
        try:
            await account.login()
        except aioaseko.InvalidAuthCredentials:
            print("The username or password you entered is wrong.")
            return
        units = await account.get_units()
        for unit in units:
            print(unit.name)
            await unit.get_state()
            print(f"Water flow: {unit.water_flow}")
            for variable in unit.variables:
                print(variable.name, variable.current_value, variable.unit)

            if unit.has_error:
                for error in unit.errors:
                    print(error.type, error.title, error.content)

        await account.logout()

run(main())
