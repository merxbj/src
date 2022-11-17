import growattServer
from datetime import date
from growattServer import Timespan

api = growattServer.GrowattApi()

login_response = api.login("***", "***")

plant_list = api.plant_list(login_response['user']['id'])

for plant in plant_list["data"]:
    plant_id = plant["plantId"]
    device_list = api.device_list(plant_id)
    print(api.mix_detail("TPJ4CD602H", plant_id, Timespan.hour, date(2022, 10, 16)))
    #mix_info = api.mix_info("TPJ4CD602H", plant_id)
    #print(mix_info)
    #print(device_list)
    #print(api.plant_info(plant_id))
    #print(api.plant_detail(plant_id, Timespan.day, date(2022, 9, 15)))