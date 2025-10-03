package org.exbin.bined.editor.search;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CancellationException;

/**
 * Minimal Search UI panel. Add this to BinEd's docking area.
 */
public class SearchPanel extends JPanel {
    private final EditorAccessor accessor;
    private final JTextField txtQuery = new JTextField(30);
    private final JComboBox<SearchWorker.Mode> cmbMode = new JComboBox<>(SearchWorker.Mode.values());
    private final JCheckBox chkRegex = new JCheckBox("Regex");
    private final JCheckBox chkCase = new JCheckBox("Case sensitive");
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnCancel = new JButton("Cancel");
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final DefaultListModel<SearchResult> listModel = new DefaultListModel<>();
    private final JList<SearchResult> resultsList = new JList<>(listModel);

    private SearchWorker currentWorker;

    public SearchPanel(EditorAccessor accessor) {
        this.accessor = accessor;
        setLayout(new BorderLayout());
        add(buildTopPanel(), BorderLayout.NORTH);
        add(new JScrollPane(resultsList), BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SearchResult r = resultsList.getSelectedValue();
                    if (r != null) {
                        accessor.highlightRange(r.getOffset(), r.getLength());
                    }
                }
            }
        });

        btnSearch.addActionListener(e -> startSearch());
        btnCancel.addActionListener(e -> cancelSearch());
    }

    private JPanel buildTopPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel("Query:"));
        p.add(txtQuery);
        p.add(new JLabel("Mode:"));
        p.add(cmbMode);
        p.add(chkCase);
        p.add(btnSearch);
        p.add(btnCancel);
        return p;
    }

    private void startSearch() {
        if (currentWorker != null && !currentWorker.isDone()) {
            JOptionPane.showMessageDialog(this, "Search already running");
            return;
        }
        listModel.clear();
        String q = txtQuery.getText().trim();
        if (q.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter search query");
            return;
        }

        SearchWorker.Mode mode = (SearchWorker.Mode) cmbMode.getSelectedItem();
        boolean caseSensitive = chkCase.isSelected();
        // Determine mode override: if chkRegex selected, use REGEX
        if (chkRegex.isSelected()) mode = SearchWorker.Mode.REGEX;

        currentWorker = new SearchWorker(accessor, mode, q, Charset.defaultCharset(), caseSensitive);
        currentWorker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            } else if ("state".equals(evt.getPropertyName())) {
                if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                    try {
                        currentWorker.get(); // to propagate exceptions
                        // load found results
                        List<SearchResult> results = currentWorker.getResults();
                        for (SearchResult r : results) listModel.addElement(r);
                        JOptionPane.showMessageDialog(this, "Search finished. Found: " + results.size());
                    } catch (Exception ex) {
                        if (ex.getCause() instanceof CancellationException) {
                            JOptionPane.showMessageDialog(this, "Search cancelled.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage());
                        }
                    }
                }
            }
        });

        // show incremental results by polling worker's internal result list (small convenience)
        Timer t = new Timer(300, e -> {
            List<SearchResult> cur = currentWorker.getResults();
            int existing = listModel.size();
            for (int i = existing; i < cur.size(); i++) {
                listModel.addElement(cur.get(i));
            }
        });
        t.start();

        currentWorker.execute();
    }

    private void cancelSearch() {
        if (currentWorker != null) currentWorker.cancel(true);
    }
}
