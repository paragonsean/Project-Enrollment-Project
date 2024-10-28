import pandas as pd
from datetime import datetime
from typing import List, Dict, Optional

class Section:
    def __init__(self, crn: str, enrollment: int, capacity: int, campus: str, link_code: str, offering: 'Offering', snapshot_date: datetime):
        self.crn = crn  # Unique identifier for the section within the semester
        self.enrollment = enrollment
        self.capacity = capacity
        self.campus = campus
        self.link_code = link_code  # E.g., 'A1', 'A2', 'A3' for Lecture, Recitation, Lab respectively
        self.offering = offering  # Reference to the parent Offering
        self.section_type = self.determine_section_type(link_code)
        # Create a unique ID based on the snapshot date and CRN
        self.section_id = f"{snapshot_date.strftime('%Y%m%d')}_{self.crn}"

    def determine_section_type(self, link_code: str) -> str:
        if link_code.endswith("1"):
            return "Lecture"
        elif link_code.endswith("2"):
            return "Recitation"
        elif link_code.endswith("3"):
            return "Lab"
        else:
            return "Unknown"

    def is_enrollment_exceeding_cap(self) -> bool:
        return self.enrollment > self.capacity

    # Implement hash and equality based on the unique section_id
    def __hash__(self):
        return hash(self.section_id)

    def __eq__(self, other):
        if not isinstance(other, Section):
            return False
        return self.section_id == other.section_id

    def __repr__(self):
        return f"Section(id={self.section_id}, enrollment={self.enrollment}, capacity={self.capacity}, campus='{self.campus}', type='{self.section_type}', link_code='{self.link_code}')"


class Offering:
    def __init__(self, course_id: str, xlst_group: Optional[str], professor: str, overall_cap: int):
        # Unique ID for each offering based on course_id, xlst_group, and professor
        self.offering_id = f"{course_id}_{xlst_group or 'SINGLE'}_{professor.replace(' ', '_')}"
        self.professor = professor
        self.sections: List[Section] = []
        self.overall_cap = overall_cap
        self.overall_enrollment = 0  # Total enrollment across all sections

    def add_section(self, section: Section):
        self.sections.append(section)
        self.overall_enrollment += section.enrollment  # Update the overall enrollment

    def is_enrollment_exceeding_cap(self) -> bool:
        return self.overall_enrollment > self.overall_cap

    # Implement hash and equality based on the unique offering_id
    def __hash__(self):
        return hash(self.offering_id)

    def __eq__(self, other):
        if not isinstance(other, Offering):
            return False
        return self.offering_id == other.offering_id

    def __repr__(self):
        return f"Offering(id={self.offering_id}, professor='{self.professor}', overall_enrollment={self.overall_enrollment}, overall_cap={self.overall_cap}, sections={len(self.sections)})"


class Course:
    def __init__(self, subject: str, course_number: str, title: str, cross_listed_code: Optional[str] = None):
        self.subject = subject
        self.course_number = course_number
        self.title = title
        self.cross_listed_code = cross_listed_code  # Cross-listed course code if applicable
        self.course_id = f"{self.subject}_{self.course_number}"
        self.offerings: Dict[str, Offering] = {}

    def add_offering(self, offering: Offering):
        self.offerings[offering.offering_id] = offering

    def total_enrollment(self) -> int:
        return sum(offering.overall_enrollment for offering in self.offerings.values())

    def total_capacity(self) -> int:
        return sum(offering.overall_cap for offering in self.offerings.values())

    def __repr__(self):
        return f"Course(id={self.course_id}, title='{self.title}', cross_listed='{self.cross_listed_code}', offerings={len(self.offerings)})"



class Snapshot:
    def __init__(self, snapshot_date: datetime, semester: 'Semester'):
        self.snapshot_date = snapshot_date
        self.semester = semester  # Reference to the parent Semester
        self.courses: Dict[str, Course] = {}

    def add_course(self, course: Course):
        self.courses[course.course_id] = course

    def get_total_enrollment(self) -> int:
        return sum(course.total_enrollment() for course in self.courses.values())

    def get_total_capacity(self) -> int:
        return sum(course.total_capacity() for course in self.courses.values())

    def __repr__(self):
        return f"Snapshot(date={self.snapshot_date}, semester={self.semester.semester_code}, courses={len(self.courses)})"


class Semester:
    def __init__(self, semester_code: str):
        self.semester_code = semester_code
        self.snapshots: List[Snapshot] = []

    def add_snapshot(self, snapshot: Snapshot):
        self.snapshots.append(snapshot)

    def __repr__(self):
        return f"Semester(code={self.semester_code}, snapshots={len(self.snapshots)})"

def load_snapshot_from_csv(file_path: str, semester: Semester) -> Snapshot:
    # Load the CSV data
    df = pd.read_csv(file_path)

    # Extract the snapshot date from the filename
    date_str = file_path.split('/')[-1].split('.')[0]
    snapshot_date = datetime.strptime(date_str, "%Y-%m-%d")
    snapshot = Snapshot(snapshot_date=snapshot_date, semester=semester)

    course_dict = {}

    for _, row in df.iterrows():
        subj, crse = row['SUBJ'], row['CRSE']
        course_id = f"{subj}_{crse}"

        if course_id not in course_dict:
            course_dict[course_id] = Course(
                subject=subj,
                course_number=crse,
                title=row['TITLE'],
                cross_listed_code=row.get('XLST GROUP')
            )

        course = course_dict[course_id]
        xlst_group = row.get('XLST GROUP')
        professor = row['INSTRUCTOR']
        offering_id = f"{course_id}_{xlst_group or 'SINGLE'}_{professor.replace(' ', '_')}"

        if offering_id not in course.offerings:
            offering = Offering(
                course_id=course_id,
                xlst_group=xlst_group,
                professor=professor,
                overall_cap=row['OVERALL CAP']
            )
            course.add_offering(offering)
        else:
            offering = course.offerings[offering_id]

        # Handle missing link_code with a default value
        link_code = row['LINK'] if pd.notna(row['LINK']) else 'Unknown'

        section = Section(
            crn=row['CRN'],
            enrollment=row['ENR'],
            capacity=row['XLST CAP'],
            campus=row['CAMPUS'],
            link_code=link_code,
            offering=offering,
            snapshot_date=snapshot_date
        )
        offering.add_section(section)

    for course in course_dict.values():
        snapshot.add_course(course)

    semester.add_snapshot(snapshot)
    return snapshot



def main():
    # Create a Semester instance and load a snapshot into it
    semester = Semester("202410")  # Example semester code
    file_path = '2024-04-27.csv'  # Replace with the actual file path
    snapshot = load_snapshot_from_csv(file_path, semester)

    # Display semester and snapshot details
    print("Semester Details:")
    print(semester)
    print("\nSnapshot Details:")
    print(snapshot)
    print(f"Total Enrollment: {snapshot.get_total_enrollment()}")
    print(f"Total Capacity: {snapshot.get_total_capacity()}")

    # Print detailed information for each course, offering, and section
    print("\nCourses, Offerings, and Sections:")
    for course_id, course in snapshot.courses.items():
        print(f"\nCourse: {course}")
        for offering_id, offering in course.offerings.items():
            print(f"  Offering: {offering}")
            for section in offering.sections:
                print(f"    Section: {section}")


if __name__ == "__main__":
    main()
