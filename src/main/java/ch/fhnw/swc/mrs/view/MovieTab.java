package ch.fhnw.swc.mrs.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;

/**
 * Implementation of the tab "Movie"
 */
public class MovieTab extends JPanel {

    JButton saveMovie = new JButton("Save");
    JButton newMovie = new JButton("New");
    JButton deleteMovie = new JButton("Delete");
    JButton editMovie = new JButton("Edit");
    JComboBox<Integer> ageRating = new JComboBox<>();
    JTextField releaseDate = new JTextField();
    JTextField title = new JTextField();
    JButton cancel = new JButton("Cancel");
    JTable movieTable = new JTable();

    private MovieController controller;

    protected static final DateTimeFormatter SDF = DateTimeFormatter.ISO_DATE;
    private static final String[] HEADERS = new String[] { "Movie ID", "Title", "Release Date", "Is Rented?",
            "Age Rating" };
    private static final Class<?>[] TYPES = new Class[] { Long.class, String.class, Object.class, Boolean.class,
            Integer.class };
    private static final Integer[] AGE_RATINGS = new Integer[] { 0, 6, 12, 14, 16, 18 };

    /**
     * Create and initialize the content of the "Movie" tab.
     * 
     * @param controller the controller that manages this view.
     */
    public MovieTab(MovieController controller) {
        this.setName("Movies");
        this.controller = controller;
        controller.setView(this);
        createUIElements();
        setLayout();
    }

    private void createUIElements() {
        String today = LocalDate.now().format(SDF);

        title.setToolTipText("Please enter or re-enter the title of the movie.");
        releaseDate.setText(today);
        releaseDate.setToolTipText("Please enter the release date of the movie.");
        ageRating.setModel(new DefaultComboBoxModel<Integer>(AGE_RATINGS));
        ageRating.setToolTipText("Please select a minimum age to be allowed to rent this movie.");

        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleCancel();
            }
        });

        editMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleEdit();
            }
        });

        deleteMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleDelete();
            }
        });

        newMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleNew();
            }
        });

        saveMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.handleSave();
            }
        });

        DefaultTableModel tm = new DefaultTableModel() {
            public Class<?> getColumnClass(int colIndex) {
                return TYPES[colIndex];
            }
        };
        tm.setColumnIdentifiers(HEADERS);
        movieTable.setModel(tm);
        movieTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                controller.handleTableClicked(movieTable.getRowCount() > 0);
            }
        });
    }

    private void setLayout() {
        JLabel titleLabel = new JLabel("Movie Title:");
        JLabel releaseDateLabel = new JLabel("Release Date:");
        JLabel priceCatLabel = new JLabel("Price category:");
        JLabel ageRatingLabel = new JLabel("Age rating:");

        GroupLayout layout = new GroupLayout(this);
        JScrollPane scrollPane = new JScrollPane(movieTable);
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                        layout.createSequentialGroup().addContainerGap(149, Short.MAX_VALUE).addComponent(cancel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(newMovie)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(editMovie)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(deleteMovie)
                                .addGap(18, 18, 18).addComponent(saveMovie).addContainerGap())
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(priceCatLabel)
                                .addComponent(releaseDateLabel).addComponent(titleLabel).addComponent(ageRatingLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(title, GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(releaseDate, GroupLayout.Alignment.LEADING)
                                        .addComponent(ageRating, GroupLayout.Alignment.LEADING)))
                        .addContainerGap())
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE).addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(titleLabel)
                                .addComponent(title, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(releaseDateLabel).addComponent(releaseDate, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(ageRatingLabel).addComponent(ageRating, GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(priceCatLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(saveMovie)
                                .addComponent(deleteMovie).addComponent(editMovie).addComponent(newMovie)
                                .addComponent(cancel))
                        .addContainerGap()));
    }

}
