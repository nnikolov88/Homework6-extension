package fmi.informatics.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SortOrder;

import fmi.informatics.comparators.AgeComparator;
import fmi.informatics.comparators.EgnComparator;
import fmi.informatics.comparators.HeightComparator;
import fmi.informatics.comparators.NameComparator;
import fmi.informatics.comparators.PersonComparator;
import fmi.informatics.comparators.WeightComparator;
import fmi.informatics.enums.EType;
import fmi.informatics.extending.Person;
import fmi.informatics.extending.Professor;
import fmi.informatics.extending.Student;
import fmi.informatics.extending.Student.StudentGenerator;
import fmi.informatics.util.FileReader;

// създаваме клас PersonDataGUI
public class PersonDataGUI {
	
	public static Person[] people;
	JTable table;
	PersonDataModel personDataModel;
	public static Person[] samepeople;

	public static void main(String[] args) {
		// Ако извикваме четенето от файл, трябва да закоментираме метода getPeople()
		getPeople();
		
		// TODO Извикваме четенето от файл
		// people = FileReader.readPeople();
		
		// TODO Извикваме писането във файл
		initializeData();
		
		PersonDataGUI gui = new PersonDataGUI();
		gui.createAndShowGUI();
	}
	
	// TODO Добавяме писането/четенето във файл
	private static void initializeData() {
		if (!FileReader.isFileExists()) {
			FileReader.createPersonFile();
		}
		
		FileReader.writePeople(people);
	}
	
	public static void getPeople() {
		people = new Person[8];
		//ArrayList<Person> reset = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Person student = Student.StudentGenerator.make();
			people[i] = student;
			//reset.add(student);
		}
		
		for (int i = 4; i < 8; i++) {
			Person professor = Professor.ProfessorGenerator.make();
			people[i] = professor;
			//reset.add(professor);
		}
		samepeople = people;
	}
	
	public void createAndShowGUI() {
		JFrame frame = new JFrame("Таблица с данни за хора");
		frame.setSize(500, 500);
		
		JLabel label = new JLabel("Списък с потребители", JLabel.CENTER);
		
		personDataModel = new PersonDataModel(people);
		table = new JTable(personDataModel);
		
		JScrollPane scrollPane = new JScrollPane(table);
		
		// Добавяме бутон за сортиране по години с Comparable interface
		JButton buttonSortAge = new JButton("Сортирай по години");

		// Добавяме бутон за сортиране
		JButton buttonSort = new JButton("Сортирай");
		
		// TODO Добавяме бутон за филтриране
		JButton buttonFilter = new JButton("Филтрирай");
		
		// Добавяме бутон за възстановяване
		JButton buttonReset = new JButton("Възстанови");
		
		//Добавяне mouse listener
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JOptionPane.showMessageDialog(frame, people[table.getSelectedRow()]
							+ "\nЕГН: " + table.getValueAt(table.getSelectedRow(), 1).toString()
							+ "\nГодини: " + table.getValueAt(table.getSelectedRow(), 2).toString()
							+ "\nВисочина: " + table.getValueAt(table.getSelectedRow(), 3).toString() + " cm."
							+ "\nТегло: " + table.getValueAt(table.getSelectedRow(), 4).toString() + " kg."
		
							);
				}
			}
		});
		
		// TODO Добавяме панел, където ще поставим бутоните
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(buttonReset);
		buttonsPanel.add(buttonSortAge);
		buttonsPanel.add(buttonSort);
		buttonsPanel.add(buttonFilter);
		
		Container container = frame.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(label, BorderLayout.NORTH);
		container.add(scrollPane, BorderLayout.CENTER);
		// TODO Добавяме панелът с бутоните в контейнера
		container.add(buttonsPanel, BorderLayout.SOUTH);
		
		buttonSortAge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Arrays.sort(people);
				table.repaint();
			}
		});
		
		// TODO Променяме диалога за сортиране
		final JDialog sortDialog = new CustomDialog(getSortText(), this, EType.SORT);
		
		// TODO Добавяме диалог за филтрацията
		final JDialog filterDialog = new CustomDialog(getFilterText(), this, EType.FILTER);
		
		// Добавяме listener към бутона за сортиране
		buttonSort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sortDialog.pack();
				sortDialog.setVisible(true);
			}
		});
		
		// TODO Добавяме listener за филтрация
		buttonFilter.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				filterDialog.pack();
				filterDialog.setVisible(true);
			}
		});
		
		// Добавяме listener към бутона за възстановяване
		buttonReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		
		frame.setVisible(true);
	}
	
	public void reset() {
		this.personDataModel = new PersonDataModel(samepeople);
		table.setModel(this.personDataModel);
		table.repaint();
	}
	
	// TODO Добавяме метод за филтриране
	public void filterTable(int intValue, JTable table, Person[] people) {

		switch (intValue) {
			case 1: 
				this.personDataModel = new PersonDataModel(filterData(people, Student.class));
				break;
			case 2: 
				this.personDataModel = new PersonDataModel(filterData(people, Professor.class));
				break;
			case 3: 
				this.personDataModel = new PersonDataModel(filterData(people, Person.class));
				break;
		}
		
		
	}
	
	// TODO Добавяме помощен метод за филтриране
	private Person[] filterData(Person[] persons, Class<?> clazz) {
		ArrayList<Person> filteredData = new ArrayList<>();
		
		for (int i = 0; i < persons.length; i++) {
			
			if (clazz == Person.class) {
				// Тук обхващаме филтрирането на "Други"
				if (!(persons[i] instanceof Student) && !(persons[i] instanceof Professor)) {
					filteredData.add(persons[i]);
				}
			} else if (clazz.isInstance(persons[i])) { // Филтриране по студент или професор
				filteredData.add(persons[i]);
			}
		}
		
		// Преобразуваме списъка в масив
		Person[] filteredDataArray = new Person[filteredData.size()];
		filteredDataArray = filteredData.toArray(filteredDataArray);
		return filteredDataArray;
	}

	public void sortTable(int intValue, JTable table, Person[] people) {
		PersonComparator comparator = null;
		
		switch (intValue) {
		case 1: 
			comparator = new NameComparator(); 
			comparator.setSortOrder(SortOrder.ASCENDING); //сортираме Имената по възходящ ред
			break;
		case 2: 
			comparator = new EgnComparator();
			comparator.setSortOrder(SortOrder.ASCENDING); //сортираме ЕГН по възходящ ред
			break;
		case 3:
			comparator = new HeightComparator();
			comparator.setSortOrder(SortOrder.DESCENDING); //сортираме Височината по низходящ ред
			break;
		case 4: 
			comparator = new WeightComparator();
			comparator.setSortOrder(SortOrder.DESCENDING);//сортираме Теглото по низходящ ред
			break;
		case 5:
			comparator = new AgeComparator();
			comparator.setSortOrder(SortOrder.ASCENDING);//сортираме Годините по възходящ ред
			break;
		}

		if (comparator == null) { // Ако стойността е null - сортирай по подразбиране
			Arrays.sort(people); // Сортировка по подразбиране по години
		} else {
			Arrays.sort(people, comparator);
		}
		
		table.repaint();	
	}
	
	private static String getSortText() {
		return "Моля, въведете цифрата на колоната, по която да се сортират данните: \n" +
				" 1 - Име \n" +
				" 2 - ЕГН \n" +
				" 3 - Височина \n" +
				" 4 - Тегло \n" +
				" 5 - Години \n"; 
	}
	
	// TODO Добавяме текст, който да се визуализира в диалога за филтриране
	private static String getFilterText(){
		return "Моля, въведете цифрата на филтъра, който искате да използвате: \n" +
			   " 1 - Студенти \n" +
			   " 2 - Преподаватели \n" + 
			   " 3 - Други \n";
	}
}
