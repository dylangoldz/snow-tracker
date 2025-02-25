import http.client
import os
from dotenv import load_dotenv
import json  # Import json to parse the response

load_dotenv()  # Load environment variables from .env file

# Retrieve the API key from the environment
api_key = os.getenv("RAPIDAPI_KEY")

conn = http.client.HTTPSConnection("ski-resorts-and-conditions.p.rapidapi.com")

headers = {
    'x-rapidapi-key': api_key,  # Use the API key from the .env file
    'x-rapidapi-host': "ski-resorts-and-conditions.p.rapidapi.com"
}

# Initialize page number
current_page = 1

# Open a file to write the data
with open('scripts/ski_resorts.txt', 'a') as file:  # Append mode
    resorts_list = []  # List to hold resort data
    while True:  # Loop to fetch all pages
        # Request data for the current page
        conn.request("GET", f"/v1/resort?page={current_page}", headers=headers)
        res = conn.getresponse()
        data = res.read()

        # Parse the JSON response
        response_data = json.loads(data.decode("utf-8"))

        # Write each resort's details to the file
        for resort in response_data['data']:
            # Extract coordinates
            location = resort.get('location', {})
            country = location.get('country', '')  # Get the country information
            latitude = location.get('latitude', 'N/A')
            longitude = location.get('longitude', 'N/A')

            # Filter out non-US countries
            if country != 'US':
                continue  # Skip to the next resort if it's not in the US

            # Call NOAA API
            noaa_conn = http.client.HTTPSConnection("api.weather.gov")
            noaa_conn.request("GET", f"/points/{latitude},{longitude}")
            noaa_res = noaa_conn.getresponse()

            # Check the response status
            if noaa_res.status == 200:
                noaa_data = json.loads(noaa_res.read().decode("utf-8"))
            else:
                print(f"Error fetching data from NOAA API: {noaa_res.status} {noaa_res.reason}")
                continue  # Skip to the next resort if there's an error

            # Create Resort JSON object
            resort_json = {
                "id": resort.get('slug', 'N/A'),
                "name": resort.get('name', 'N/A'),
                "regionId": resort.get('region', 'N/A'),
                "office": noaa_data.get('properties', {}).get('office', 'N/A'),
                "gridX": noaa_data.get('properties', {}).get('gridX', 'N/A'),
                "gridY": noaa_data.get('properties', {}).get('gridY', 'N/A')
            }
            resorts_list.append(resort_json)

            # Write resort details to the file
            file.write(json.dumps(resort_json) + "\n")  # Write JSON to file

        # Check for the next page
        next_page = response_data.get('next_page')
        if next_page:
            current_page += 1  # Increment the page number
        else:
            print("No more pages available.")
            break  # Exit the loop if no more pages

# Optionally, save the resorts_list to a JSON file
with open('scripts/resorts.json', 'w') as json_file:
    json.dump(resorts_list, json_file, indent=4)  # Save all resorts to a JSON file
