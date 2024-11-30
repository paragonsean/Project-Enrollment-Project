import os
import random
import datetime
import csv
import subprocess
from datetime import datetime, timedelta

# Helper function to create a snapshot CSV file for each semester date
def create_snapshot_csv(snapshot_date, snapshot_dir):
    snapshot_filename = os.path.join(snapshot_dir, f"{snapshot_date.strftime('%Y-%m-%d')}_snapshot.csv")
    
    with open(snapshot_filename, 'w') as file:
        file.write("CRN,SUBJ,CRSE,XLST CAP,ENR,LINK,XLST GROUP,OVERALL CAP,OVERALL ENR\n")
        
        # Generate random data for each course snapshot (5 courses per snapshot)
        for crn in range(1, 6):  # 5 courses
            subj = random.choice(["CS", "MATH", "BIO", "ENG", "HIST"])
            crse = random.randint(100, 300)
            xlst_cap = random.randint(20, 50)
            enr = random.randint(0, xlst_cap)
            link = f"{random.choice('ABCDE')}{random.randint(1, 3)}"  # Lecture/lab/recitation
            xlst_group = random.choice(["", "Group1", "Group2"])  # Cross-listing group
            overall_cap = random.randint(50, 100)
            overall_enr = random.randint(0, overall_cap)
            
            # Write the random data to the CSV file
            file.write(f"{10000+crn},{subj},{crse},{xlst_cap},{enr},{link},{xlst_group},{overall_cap},{overall_enr}\n")
    
    print(f"Created snapshot CSV for {snapshot_date.strftime('%Y-%m-%d')} at {snapshot_filename}")

# Function to create a directory for each semester with its corresponding data
def create_semester_directory(semester_code, start_date, num_days=10):
    base_dir = f"./historical_semesters/{semester_code}"
    os.makedirs(base_dir, exist_ok=True)
    
    # Create the dates.txt file
    dates_file = os.path.join(base_dir, 'dates.txt')
    add_deadline_date = start_date + datetime.timedelta(days=num_days-1)  # Add deadline 9 days after start
    with open(dates_file, 'w') as file:
        file.write(start_date.strftime("%Y-%m-%d") + '\n')
        file.write(add_deadline_date.strftime("%Y-%m-%d") + '\n')
    
    # Create snapshot CSV files (10 snapshot files for 10 days)
    snapshot_dir = os.path.join(base_dir, 'snapshots')
    os.makedirs(snapshot_dir, exist_ok=True)
    
    # Generate 10 snapshots, one for each day
    for i in range(num_days):
        snapshot_date = start_date + datetime.timedelta(days=i)
        create_snapshot_csv(snapshot_date, snapshot_dir)  # Reuse the CSV generation method
    
    print(f"Created semester directory structure for {semester_code}")

# Run the Java program for projections
import subprocess

# Function to run the Java program for projections
def run_java_program(historical_dirs, current_dir, report_file, last_day=None):
    command = ["java", "-jar", "YourJavaProgram.jar"]  # Adjust with actual Java jar location and program name
    
    # Add historical directories to the command
    command.extend(historical_dirs)
    
    # Add the current semester directory
    command.append(current_dir)
    
    # Add the report file path
    command.append(report_file)
    
    # Optionally, add the last_day argument if provided
    if last_day:
        command.append(f"--last_day={last_day}")
    
    # Run the command and capture output
    result = subprocess.run(command, capture_output=True, text=True)
    
    # Print the output of the Java program
    print("Java Program Output:")
    print(result.stdout)
    
    # Save the error output if any
    if result.stderr:
        print("Java Program Error Output:")
        print(result.stderr)

# Example directories and report file
historical_dirs = [
    "semesters/202210",  # Example historical semester 1
    "semesters/202220",  # Example historical semester 2
    "semesters/202230",  # Example historical semester 3
    "semesters/202240"   # Example historical semester 4
]
current_dir = "semesters/202250"  # Example current semester directory
report_file = "projection_report.csv"  # Path to save the projection report

# Optional: Last day to examine data from current semester (YYYY-MM-DD)
last_day = "2022-03-15"  # This could be a dynamic date or None if not needed

# Run the Java program with historical data, current semester data, and report path
run_java_program(historical_dirs, current_dir, report_file, last_day)


# Compare the actual report with the expected output
def compare_outputs(actual_file, expected_file):
    with open(actual_file, 'r') as f1, open(expected_file, 'r') as f2:
        actual_lines = f1.readlines()
        expected_lines = f2.readlines()
        
        if actual_lines == expected_lines:
            print("Test passed: Output matches expected results.")
        else:
            print("Test failed: Output does not match expected results.")
            from difflib import unified_diff
            for diff in unified_diff(actual_lines, expected_lines, fromfile='actual', tofile='expected'):
                print(diff)

# Main function to create historical semester data
def main():
    # Set the starting date for historical data
    start_date = datetime.date(2022, 1, 1)  # Example start date for the first semester
    
    # Generate 4 historical semesters, each 10 days long
    historical_semesters = ['202210', '202220', '202230', '202240']
    
    for semester_code in historical_semesters:
        create_semester_directory(semester_code, start_date)
        # Increment the start_date by 15 days for each new semester
        start_date += datetime.timedelta(days=15)
        
    print("Historical semester data created.")

if __name__ == "__main__":
    main()
