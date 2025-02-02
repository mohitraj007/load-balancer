import time
from flask import Flask
import sys

app = Flask(__name__)

# Default health check cycles
health_cycles = {
    8081: {"ok_duration": 10, "fail_duration": 10},
    8082: {"ok_duration": 15, "fail_duration": 5},
    8083: {"ok_duration": 20, "fail_duration": 10},
}

# Initialize a dictionary to store the last fail times for each port
last_fail_times = {port: time.time() for port in health_cycles}

@app.route('/health')
def health_check():
    global last_fail_times
    current_time = time.time()
    port = int(sys.argv[1]) if len(sys.argv) > 1 else 8081  # Default to port 8081
    elapsed_time = current_time - last_fail_times[port]
    ok_duration = health_cycles[port]["ok_duration"]

    if elapsed_time >= ok_duration:
        last_fail_times[port] = current_time
        return "FAIL"
    return "OK"

@app.route('/data')
def get_data():
    return f"Response from backend server: {time.time()}"

if __name__ == '__main__':
    port = int(sys.argv[1]) if len(sys.argv) > 1 else 8081  # Default to port 8081
    app.run(host='0.0.0.0', port=port)
