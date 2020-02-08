import io

with io.open(r"c:\temp\eci\waylet pilot\ws\ServiceHost.log", "r", encoding="utf-8") as f:
    lines = f.readlines()

nextLineJson = False
for line in lines:
    if "TransactionServiceClient.PerformRequest Request:" in line:
        nextLineJson = True
    elif nextLineJson:
        print(line.strip())
        nextLineJson = False
