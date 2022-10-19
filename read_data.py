import json
import pandas as pd
import requests

df = pd.read_csv("COMET_markiert_merged.csv")

print(df)

documents = []

for i, row in df.iterrows():
    us_string = row[0].encode("ascii", "ignore").decode().replace("\\", "").replace("\"", "")
    ac_string = row[1].encode("ascii", "ignore").decode().replace("\\", "").replace("\"", "")
    
    documents.append({"number": i, "user_story": us_string, "acceptance_criterion": ac_string})

request_body = {"dataset":{"documents":documents}, "params": {"debug": False}}

print(json.dumps(request_body))

response_body = requests.post("http://localhost:9696/hitec/completeness/acceptance-criteria/run", json=request_body).json()

with open("output.txt", "w+") as f:
    f.write(json.dumps(response_body))