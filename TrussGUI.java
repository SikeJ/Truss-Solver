import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

/**
 * TrussGUI - a Swing front end for the Truss-Solver code base.
 *
 * Put this file in the same folder as the existing Truss-Solver sources
 * (Nodes, Beam, Force, List, Stack, GaussElim ...) and run:
 *
 *      javac *.java
 *      java TrussGUI
 *
 * The calculation is NOT re-implemented here: TrussEngine (bottom of this file)
 * builds real Nodes / Beam / Force objects and follows the same procedure as
 * Main.java -
 *      1. support reactions from moment balance   (List<Force> of applied loads)
 *      2. method of joints, one joint at a time    (Stack<Nodes>, GaussElim for 2 unknowns)
 *
 * Conventions: +X right, +Y up. Loads/reactions are positive in +X / +Y.
 * Member force: positive = tension, negative = compression.
 */
public class TrussGUI extends JFrame {

    // ==================================================================
    //  GUI MODEL (plain data; the solver objects are rebuilt on every Solve)
    // ==================================================================
    static final int NONE = 0, PIN = 1, ROLLER = 2;

    static class PNode {
        double x, y, fx, fy;
        int sup = NONE;
        PNode(double x, double y) { this.x = x; this.y = y; }
    }

    static class PMember {
        PNode a, b;
        double force;
        PMember(PNode a, PNode b) { this.a = a; this.b = b; }
    }

    enum Tool { SELECT, NODE, MEMBER, PIN, ROLLER, LOAD, DELETE }

    // ---- TEXT SIZE: change this one number (1.0 = original, 1.5 = 50% bigger, 2.0 = double) ----
    static final float FONT_SCALE = 1.5f;
    static float fs(float size) { return size * FONT_SCALE; }
    static int px(int size) { return Math.round(size * FONT_SCALE); }

    final java.util.ArrayList<PNode> nodes = new java.util.ArrayList<>();
    final java.util.ArrayList<PMember> members = new java.util.ArrayList<>();
    TrussEngine.Result result;
    Tool tool = Tool.NODE;

    // widgets
    final Canvas canvas = new Canvas();
    final JTextField tfSnap = new JTextField("1", 5);
    final JComboBox<String> cbUnit = new JComboBox<>(new String[]{"lbf", "N", "kN"});
    final JLabel status = new JLabel(" Ready.");
    final JTextArea log = new JTextArea();
    final NodeTableModel nodeModel = new NodeTableModel();
    final ResultTableModel memberModel = new ResultTableModel(
            new String[]{"Member", "Length", "Force", "Type"});
    final ResultTableModel reactModel = new ResultTableModel(
            new String[]{"Node", "Rx", "Ry"});

    static char nameOf(int i) { return TrussEngine.nameOf(i); }

    String unit() { return (String) cbUnit.getSelectedItem(); }

    // ==================================================================
    //  FRAME
    // ==================================================================
    public TrussGUI() {
        super("Truss Solver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildToolPanel(), BorderLayout.WEST);
        add(canvas, BorderLayout.CENTER);
        add(buildTabs(), BorderLayout.EAST);

        status.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        add(status, BorderLayout.SOUTH);

        setSize(Math.min(px(1360), 1900), Math.min(px(800), 1050));
        setLocationRelativeTo(null);
        loadExample(0);
    }

    JPanel buildToolPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        ButtonGroup bg = new ButtonGroup();
        addTool(p, bg, "Select / Move", Tool.SELECT, "Drag a node to move it");
        addTool(p, bg, "Add Node", Tool.NODE, "Click empty space to add a node");
        addTool(p, bg, "Add Member", Tool.MEMBER, "Click two nodes to join them with a member");
        addTool(p, bg, "Pin Support", Tool.PIN, "Click a node (click again to remove). Reacts in X and Y");
        addTool(p, bg, "Roller Support", Tool.ROLLER, "Click a node (click again to remove). Reacts in Y only");
        addTool(p, bg, "Apply Load", Tool.LOAD, "Click a node to enter Fx / Fy");
        addTool(p, bg, "Delete", Tool.DELETE, "Click a node or member to delete it");
        p.add(Box.createVerticalStrut(12));

        JPanel opts = new JPanel(new GridLayout(0, 2, 4, 4));
        opts.setBorder(BorderFactory.createTitledBorder("Options"));
        opts.add(new JLabel("Grid snap")); opts.add(tfSnap);
        opts.add(new JLabel("Force unit")); opts.add(cbUnit);
        opts.setMaximumSize(new Dimension(px(230), px(90)));
        opts.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbUnit.addActionListener(e -> { clearResults(); nodeModel.fireTableDataChanged(); canvas.repaint(); });
        p.add(opts);
        p.add(Box.createVerticalStrut(12));

        JButton solve = bigButton("SOLVE TRUSS");
        solve.setFont(solve.getFont().deriveFont(Font.BOLD, fs(14f)));
        solve.addActionListener(e -> doSolve());
        p.add(solve);

        JButton fit = bigButton("Zoom to Fit");
        fit.addActionListener(e -> canvas.fit());
        p.add(fit);

        JButton ex = bigButton("Load Example \u25BE");
        JPopupMenu menu = new JPopupMenu();
        JMenuItem e0 = new JMenuItem("Truss from Main.java (A\u2013E)");
        JMenuItem e1 = new JMenuItem("Warren truss");
        e0.addActionListener(e -> loadExample(0));
        e1.addActionListener(e -> loadExample(1));
        menu.add(e0); menu.add(e1);
        ex.addActionListener(e -> menu.show(ex, 0, ex.getHeight()));
        p.add(ex);

        JButton clear = bigButton("Clear All");
        clear.addActionListener(e -> {
            nodes.clear(); members.clear(); canvas.pending = null;
            modelChanged();
        });
        p.add(clear);

        p.add(Box.createVerticalStrut(14));
        JTextArea help = new JTextArea(
                "Tips\n\u2022 Mouse wheel: zoom\n\u2022 Right-drag: pan\n\u2022 Edit coordinates and loads\n   directly in the Nodes tab\n"
              + "\u2022 Needs exactly 1 pin + 1 roller\n\u2022 Members + 3 = 2 \u00D7 joints");
        help.setEditable(false); help.setOpaque(false);
        help.setFont(help.getFont().deriveFont(fs(11.5f)));
        help.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(help);
        return p;
    }

    JButton bigButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(px(230), px(34)));
        return b;
    }

    void addTool(JPanel p, ButtonGroup bg, String text, Tool t, String tip) {
        JToggleButton b = new JToggleButton(text, t == tool);
        b.setToolTipText(tip);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(px(230), px(30)));
        b.addActionListener(e -> {
            tool = t; canvas.pending = null; status.setText(" " + tip); canvas.repaint();
        });
        bg.add(b); p.add(b); p.add(Box.createVerticalStrut(2));
    }

    JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();

        JTable nt = new JTable(nodeModel);
        nt.setRowHeight(px(24));
        nt.getColumnModel().getColumn(5).setCellEditor(
                new DefaultCellEditor(new JComboBox<>(new String[]{"None", "Pin", "Roller"})));
        JPanel nodesTab = new JPanel(new BorderLayout());
        nodesTab.add(new JScrollPane(nt), BorderLayout.CENTER);
        JLabel nl = new JLabel("<html>&nbsp;Double-click a cell to edit. Fx + right, Fy + up.</html>");
        nl.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        nodesTab.add(nl, BorderLayout.SOUTH);

        JTable mt = new JTable(memberModel); mt.setRowHeight(px(22));
        JTable rt = new JTable(reactModel);  rt.setRowHeight(px(22));
        JScrollPane s1 = new JScrollPane(mt);
        s1.setBorder(BorderFactory.createTitledBorder("Member forces (+ tension / \u2212 compression)"));
        JScrollPane s2 = new JScrollPane(rt);
        s2.setBorder(BorderFactory.createTitledBorder("Support reactions"));
        JPanel results = new JPanel(new GridLayout(2, 1, 0, 6));
        results.add(s1); results.add(s2);

        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, Math.round(fs(12f))));

        tabs.addTab("Nodes", nodesTab);
        tabs.addTab("Results", results);
        tabs.addTab("Solver Log", new JScrollPane(log));
        tabs.setPreferredSize(new Dimension(px(430), 0));
        return tabs;
    }

    // ==================================================================
    //  MODEL ACTIONS
    // ==================================================================
    void clearResults() {
        result = null;
        for (PMember m : members) m.force = 0;
        memberModel.setRows(new Object[0][]);
        reactModel.setRows(new Object[0][]);
    }

    void modelChanged() {
        clearResults();
        nodeModel.fireTableDataChanged();
        canvas.repaint();
    }

    void loadExample(int which) {
        nodes.clear(); members.clear(); canvas.pending = null;
        if (which == 0) {
            // Same geometry as Main.java: A(0,0) B(50,0) C(25,90) D(-75,25) E(15,5)
            nodes.add(new PNode(0, 0));    // A
            nodes.add(new PNode(50, 0));   // B
            nodes.add(new PNode(25, 90));  // C
            nodes.add(new PNode(-75, 25)); // D
            nodes.add(new PNode(15, 5));   // E
            nodes.get(0).sup = PIN;
            nodes.get(1).sup = ROLLER;
            nodes.get(3).fy = -200;
            nodes.get(4).fy = -300;
            int[][] mm = {{0, 1}, {0, 2}, {1, 2}, {0, 3}, {3, 2}, {2, 4}, {4, 1}}; // AB AC BC AD DC CE EB
            for (int[] e : mm) members.add(new PMember(nodes.get(e[0]), nodes.get(e[1])));
            tfSnap.setText("1");
        } else {
            double[][] xy = {{0, 0}, {10, 0}, {20, 0}, {5, 8}, {15, 8}};
            for (double[] p : xy) nodes.add(new PNode(p[0], p[1]));
            nodes.get(0).sup = PIN;
            nodes.get(2).sup = ROLLER;
            nodes.get(3).fy = -1000;
            nodes.get(4).fy = -1500;
            nodes.get(4).fx = 400;
            int[][] mm = {{0, 1}, {1, 2}, {3, 4}, {0, 3}, {3, 1}, {1, 4}, {4, 2}};
            for (int[] e : mm) members.add(new PMember(nodes.get(e[0]), nodes.get(e[1])));
            tfSnap.setText("1");
        }
        log.setText("");
        modelChanged();
        canvas.needFit = true;
        canvas.repaint();
        status.setText(" Example loaded - press SOLVE TRUSS.");
    }

    void doSolve() {
        int n = nodes.size(), m = members.size();
        double[] xs = new double[n], ys = new double[n], fx = new double[n], fy = new double[n];
        int[] sup = new int[n];
        for (int i = 0; i < n; i++) {
            PNode p = nodes.get(i);
            xs[i] = p.x; ys[i] = p.y; fx[i] = p.fx; fy[i] = p.fy; sup[i] = p.sup;
        }
        int[][] mem = new int[m][2];
        for (int i = 0; i < m; i++) {
            mem[i][0] = nodes.indexOf(members.get(i).a);
            mem[i][1] = nodes.indexOf(members.get(i).b);
        }

        result = TrussEngine.solve(xs, ys, fx, fy, sup, mem, unit());
        log.setText(result.log.toString());
        log.setCaretPosition(0);

        if (!result.ok) {
            for (PMember pm : members) pm.force = 0;
            memberModel.setRows(new Object[0][]);
            reactModel.setRows(new Object[0][]);
            status.setText(" " + result.message);
            JOptionPane.showMessageDialog(this, result.message, "Cannot solve", JOptionPane.WARNING_MESSAGE);
            canvas.repaint();
            return;
        }

        Object[][] mrows = new Object[m][];
        for (int i = 0; i < m; i++) {
            PMember pm = members.get(i);
            pm.force = result.beamForce[i];
            double len = Math.hypot(pm.a.x - pm.b.x, pm.a.y - pm.b.y);
            String type = Math.abs(pm.force) < 1e-6 ? "Zero-force" : (pm.force > 0 ? "Tension" : "Compression");
            mrows[i] = new Object[]{result.beamName[i], String.format("%.2f", len),
                    String.format("%.2f %s", pm.force, unit()), type};
        }
        memberModel.setRows(mrows);

        java.util.ArrayList<Object[]> rr = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++)
            if (sup[i] != NONE)
                rr.add(new Object[]{String.valueOf(nameOf(i)) + (sup[i] == PIN ? " (pin)" : " (roller)"),
                        String.format("%.2f", result.rx[i]), String.format("%.2f", result.ry[i])});
        reactModel.setRows(rr.toArray(new Object[0][]));

        status.setText(String.format(" Solved. Largest joint equilibrium residual = %.2e %s", result.residual, unit()));
        canvas.repaint();
    }

    // ==================================================================
    //  TABLE MODELS
    // ==================================================================
    static String fmt(double v) {
        if (v == Math.rint(v) && Math.abs(v) < 1e9) return String.valueOf((long) v);
        String s = String.format("%.4f", v);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    class NodeTableModel extends AbstractTableModel {
        final String[] cols = {"Node", "X", "Y", "Fx", "Fy", "Support"};
        public int getRowCount() { return nodes.size(); }
        public int getColumnCount() { return cols.length; }
        public String getColumnName(int c) {
            return (c == 3 || c == 4) ? cols[c] + " (" + unit() + ")" : cols[c];
        }
        public boolean isCellEditable(int r, int c) { return c >= 1; }
        public Object getValueAt(int r, int c) {
            PNode p = nodes.get(r);
            switch (c) {
                case 0: return String.valueOf(nameOf(r));
                case 1: return fmt(p.x);
                case 2: return fmt(p.y);
                case 3: return fmt(p.fx);
                case 4: return fmt(p.fy);
                default: return p.sup == PIN ? "Pin" : p.sup == ROLLER ? "Roller" : "None";
            }
        }
        public void setValueAt(Object v, int r, int c) {
            PNode p = nodes.get(r);
            try {
                String s = String.valueOf(v).trim();
                switch (c) {
                    case 1: p.x = Double.parseDouble(s); break;
                    case 2: p.y = Double.parseDouble(s); break;
                    case 3: p.fx = Double.parseDouble(s); break;
                    case 4: p.fy = Double.parseDouble(s); break;
                    case 5: p.sup = s.equals("Pin") ? PIN : s.equals("Roller") ? ROLLER : NONE; break;
                    default: return;
                }
            } catch (NumberFormatException ex) {
                status.setText(" Invalid number: " + v);
                return;
            }
            clearResults();
            fireTableRowsUpdated(r, r);
            canvas.repaint();
        }
    }

    static class ResultTableModel extends AbstractTableModel {
        final String[] cols;
        Object[][] rows = new Object[0][];
        ResultTableModel(String[] cols) { this.cols = cols; }
        void setRows(Object[][] r) { rows = r; fireTableDataChanged(); }
        public int getRowCount() { return rows.length; }
        public int getColumnCount() { return cols.length; }
        public String getColumnName(int c) { return cols[c]; }
        public Object getValueAt(int r, int c) { return rows[r][c]; }
    }

    // ==================================================================
    //  CANVAS
    // ==================================================================
    static final Color TENSION = new Color(30, 90, 220), COMPRESSION = new Color(210, 40, 40),
            LOADC = new Color(0, 130, 0), REACTC = new Color(150, 60, 190);

    class Canvas extends JPanel {
        double scale = 5, ox = 200, oy = 500;
        PNode pending, dragging;
        Point lastPan, mouse = new Point();
        boolean needFit = true;

        Canvas() {
            setBackground(Color.WHITE);
            MouseAdapter ma = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                    if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isMiddleMouseButton(e)) {
                        lastPan = e.getPoint(); return;
                    }
                    click(e.getPoint());
                }
                public void mouseDragged(MouseEvent e) {
                    mouse = e.getPoint();
                    if (lastPan != null) {
                        ox += e.getX() - lastPan.x; oy += e.getY() - lastPan.y;
                        lastPan = e.getPoint(); repaint(); return;
                    }
                    if (dragging != null) {
                        dragging.x = snap(wx(e.getX())); dragging.y = snap(wy(e.getY()));
                        clearResults(); nodeModel.fireTableDataChanged(); repaint();
                    }
                }
                public void mouseReleased(MouseEvent e) { dragging = null; lastPan = null; }
                public void mouseMoved(MouseEvent e) {
                    mouse = e.getPoint();
                    status.setText(String.format(" x = %s,  y = %s", fmt(snap(wx(e.getX()))), fmt(snap(wy(e.getY())))));
                    if (pending != null) repaint();
                }
                public void mouseWheelMoved(MouseWheelEvent e) {
                    double f = e.getWheelRotation() < 0 ? 1.1 : 1 / 1.1;
                    double x = wx(e.getX()), y = wy(e.getY());
                    scale *= f;
                    ox = e.getX() - x * scale; oy = e.getY() + y * scale;
                    repaint();
                }
            };
            addMouseListener(ma); addMouseMotionListener(ma); addMouseWheelListener(ma);
        }

        double wx(int px) { return (px - ox) / scale; }
        double wy(int py) { return (oy - py) / scale; }
        int sx(double x) { return (int) Math.round(ox + x * scale); }
        int sy(double y) { return (int) Math.round(oy - y * scale); }

        double snap(double v) {
            double g;
            try { g = Double.parseDouble(tfSnap.getText().trim()); } catch (Exception e) { g = 0; }
            return g > 0 ? Math.round(v / g) * g : v;
        }

        void fit() {
            int w = Math.max(getWidth(), 600), h = Math.max(getHeight(), 400);
            if (nodes.isEmpty()) { scale = 5; ox = w / 2.0; oy = h / 2.0; repaint(); return; }
            double minX = 1e18, maxX = -1e18, minY = 1e18, maxY = -1e18;
            for (PNode n : nodes) {
                minX = Math.min(minX, n.x); maxX = Math.max(maxX, n.x);
                minY = Math.min(minY, n.y); maxY = Math.max(maxY, n.y);
            }
            double sw = (maxX - minX) < 1e-9 ? 1e9 : (w - px(300)) / (maxX - minX);
            double sh = (maxY - minY) < 1e-9 ? 1e9 : (h - px(260)) / (maxY - minY);
            scale = Math.min(Math.min(sw, sh), 200);
            if (scale > 1e8) scale = 20;
            ox = w / 2.0 - (minX + maxX) / 2 * scale;
            oy = h / 2.0 + (minY + maxY) / 2 * scale;
            repaint();
        }

        PNode nodeAt(Point p) {
            for (PNode n : nodes) if (Math.hypot(sx(n.x) - p.x, sy(n.y) - p.y) <= 11) return n;
            return null;
        }

        PMember memberAt(Point p) {
            for (PMember m : members)
                if (Line2D.ptSegDist(sx(m.a.x), sy(m.a.y), sx(m.b.x), sy(m.b.y), p.x, p.y) <= 5) return m;
            return null;
        }

        void click(Point p) {
            PNode hit = nodeAt(p);
            switch (tool) {
                case SELECT: dragging = hit; break;
                case NODE:
                    if (hit == null) {
                        if (nodes.size() >= TrussEngine.MAX_NODES) {
                            status.setText(" Maximum of " + TrussEngine.MAX_NODES + " nodes (A-Z, a-z).");
                            break;
                        }
                        nodes.add(new PNode(snap(wx(p.x)), snap(wy(p.y))));
                        modelChanged();
                    }
                    break;
                case MEMBER:
                    if (hit == null) { pending = null; break; }
                    if (pending == null) pending = hit;
                    else if (pending != hit) {
                        boolean exists = false;
                        for (PMember m : members)
                            if ((m.a == pending && m.b == hit) || (m.a == hit && m.b == pending)) exists = true;
                        if (!exists) { members.add(new PMember(pending, hit)); modelChanged(); }
                        pending = null;
                    }
                    break;
                case PIN: toggle(hit, PIN); break;
                case ROLLER: toggle(hit, ROLLER); break;
                case LOAD: if (hit != null) editLoad(hit); break;
                case DELETE:
                    if (hit != null) {
                        members.removeIf(m -> m.a == hit || m.b == hit);
                        nodes.remove(hit); pending = null; modelChanged();
                    } else {
                        PMember m = memberAt(p);
                        if (m != null) { members.remove(m); modelChanged(); }
                    }
                    break;
            }
            repaint();
        }

        void toggle(PNode n, int s) {
            if (n == null) return;
            n.sup = (n.sup == s) ? NONE : s;
            modelChanged();
        }

        void editLoad(PNode n) {
            JTextField fx = new JTextField(fmt(n.fx), 8), fy = new JTextField(fmt(n.fy), 8);
            JPanel pnl = new JPanel(new GridLayout(0, 2, 4, 4));
            pnl.add(new JLabel("Fx (" + unit() + ", + right):")); pnl.add(fx);
            pnl.add(new JLabel("Fy (" + unit() + ", + up):")); pnl.add(fy);
            int ok = JOptionPane.showConfirmDialog(TrussGUI.this, pnl,
                    "Load at node " + nameOf(nodes.indexOf(n)), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok == JOptionPane.OK_OPTION) {
                try {
                    n.fx = Double.parseDouble(fx.getText().trim());
                    n.fy = Double.parseDouble(fy.getText().trim());
                    modelChanged();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(TrussGUI.this, "Please enter valid numbers.");
                }
            }
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            if (needFit && getWidth() > 0) { needFit = false; fit(); }
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            drawGrid(g);

            boolean solved = result != null && result.ok;
            double maxF = 0;
            for (PMember m : members) maxF = Math.max(maxF, Math.abs(m.force));

            // members
            for (PMember m : members) {
                Color c = Color.DARK_GRAY; float w = 3f;
                if (solved) {
                    c = Math.abs(m.force) < 1e-6 ? Color.GRAY : (m.force > 0 ? TENSION : COMPRESSION);
                    w = 2.5f + (float) (4.0 * Math.abs(m.force) / (maxF == 0 ? 1 : maxF));
                }
                g.setStroke(new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(c);
                g.drawLine(sx(m.a.x), sy(m.a.y), sx(m.b.x), sy(m.b.y));
                String label = solved ? String.format("%.1f", m.force)
                        : "" + nameOf(nodes.indexOf(m.a)) + nameOf(nodes.indexOf(m.b));
                g.setFont(getFont().deriveFont(Font.BOLD, fs(12f)));
                FontMetrics fm = g.getFontMetrics();
                int mx = (sx(m.a.x) + sx(m.b.x)) / 2, my = (sy(m.a.y) + sy(m.b.y)) / 2, tw = fm.stringWidth(label);
                g.setColor(new Color(255, 255, 255, 225));
                g.fillRoundRect(mx - tw / 2 - 3, my - fm.getAscent() / 2 - 3, tw + 6, fm.getHeight(), 6, 6);
                g.setColor(solved ? c.darker() : Color.BLACK);
                g.drawString(label, mx - tw / 2, my + fm.getAscent() / 2 - 1);
            }

            if (pending != null) {
                g.setColor(new Color(0, 150, 0));
                g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{6, 6}, 0));
                g.drawLine(sx(pending.x), sy(pending.y), mouse.x, mouse.y);
            }

            for (int i = 0; i < nodes.size(); i++) {
                PNode n = nodes.get(i);
                drawSupport(g, n);
                drawLoad(g, n);
                if (solved && n.sup != NONE) drawReaction(g, n, i);
            }
            for (int i = 0; i < nodes.size(); i++) {
                PNode n = nodes.get(i);
                int x = sx(n.x), y = sy(n.y);
                g.setColor(n == pending ? new Color(0, 170, 0) : Color.WHITE);
                g.fillOval(x - 6, y - 6, 12, 12);
                g.setColor(Color.BLACK); g.setStroke(new BasicStroke(2f));
                g.drawOval(x - 6, y - 6, 12, 12);
                g.setFont(getFont().deriveFont(Font.BOLD, fs(14f)));
                g.drawString(String.valueOf(nameOf(i)), x + 9, y - 9);
            }

            g.setFont(getFont().deriveFont(fs(12f)));
            FontMetrics lf = g.getFontMetrics();
            int lx = 12, ly = lf.getAscent() + 6;
            String[] items = {"\u25A0 Tension", "\u25A0 Compression", "\u2192 Applied load", "\u2192 Reaction"};
            Color[] cols = {TENSION, COMPRESSION, LOADC, REACTC};
            for (int i = 0; i < items.length; i++) {
                g.setColor(cols[i]); g.drawString(items[i], lx, ly);
                lx += lf.stringWidth(items[i]) + 20;
            }
            g.setColor(Color.GRAY);
            g.drawString("Wheel = zoom, right-drag = pan", 12, getHeight() - 10);
        }

        void drawGrid(Graphics2D g) {
            double step = 1;
            while (step * scale < 25) step *= 2;
            while (step * scale > 120) step /= 2;
            g.setStroke(new BasicStroke(1f));
            g.setColor(new Color(236, 236, 242));
            double x1 = wx(getWidth()), y1 = wy(0), y0 = wy(getHeight());
            for (double x = Math.floor(wx(0) / step) * step; x <= x1; x += step) g.drawLine(sx(x), 0, sx(x), getHeight());
            for (double y = Math.floor(y0 / step) * step; y <= y1; y += step) g.drawLine(0, sy(y), getWidth(), sy(y));
            g.setColor(new Color(190, 190, 200));
            g.drawLine(sx(0), 0, sx(0), getHeight());
            g.drawLine(0, sy(0), getWidth(), sy(0));
        }

        void drawSupport(Graphics2D g, PNode n) {
            int x = sx(n.x), y = sy(n.y);
            g.setStroke(new BasicStroke(2f));
            if (n.sup == PIN) {
                Polygon t = new Polygon(new int[]{x, x - 14, x + 14}, new int[]{y, y + 22, y + 22}, 3);
                g.setColor(new Color(220, 220, 225)); g.fillPolygon(t);
                g.setColor(new Color(90, 90, 90)); g.drawPolygon(t);
                g.drawLine(x - 20, y + 22, x + 20, y + 22);
            } else if (n.sup == ROLLER) {
                Polygon t = new Polygon(new int[]{x, x - 14, x + 14}, new int[]{y, y + 18, y + 18}, 3);
                g.setColor(new Color(220, 220, 225)); g.fillPolygon(t);
                g.setColor(new Color(90, 90, 90)); g.drawPolygon(t);
                g.drawOval(x - 9, y + 18, 8, 8); g.drawOval(x + 1, y + 18, 8, 8);
                g.drawLine(x - 20, y + 27, x + 20, y + 27);
            }
        }

        void drawLoad(Graphics2D g, PNode n) {
            g.setColor(LOADC);
            g.setFont(getFont().deriveFont(Font.PLAIN, fs(12f)));
            int x = sx(n.x), y = sy(n.y), len = 50;
            if (Math.abs(n.fy) > 1e-12) {
                int d = n.fy > 0 ? 1 : -1;               // +1 = up (screen y decreases)
                arrow(g, x, y + d * (len + 8), x, y + d * 8);
                g.drawString(fmt(Math.abs(n.fy)) + " " + unit(), x + 6, y + d * (len + 8) + (d > 0 ? 14 : -4));
            }
            if (Math.abs(n.fx) > 1e-12) {
                int d = n.fx > 0 ? 1 : -1;
                arrow(g, x - d * (len + 8), y, x - d * 8, y);
                g.drawString(fmt(Math.abs(n.fx)) + " " + unit(), x - d * (len + 8) - (d > 0 ? 55 : -4), y - 6);
            }
        }

        void drawReaction(Graphics2D g, PNode n, int i) {
            g.setColor(REACTC);
            g.setFont(getFont().deriveFont(Font.BOLD, fs(12f)));
            int x = sx(n.x), y = sy(n.y);
            if (Math.abs(result.ry[i]) > 1e-9) {
                boolean up = result.ry[i] > 0;
                arrow(g, x + 38, up ? y + 34 : y - 14, x + 38, up ? y - 14 : y + 34);
                g.drawString(String.format("Ry=%.1f", result.ry[i]), x + 44, y + 52);
            }
            if (Math.abs(result.rx[i]) > 1e-9) {
                boolean right = result.rx[i] > 0;
                arrow(g, right ? x - 58 : x - 16, y + 44, right ? x - 16 : x - 58, y + 44);
                g.drawString(String.format("Rx=%.1f", result.rx[i]), x - 62, y + 62);
            }
        }

        void arrow(Graphics2D g, int x1, int y1, int x2, int y2) {
            g.setStroke(new BasicStroke(2f));
            g.drawLine(x1, y1, x2, y2);
            double a = Math.atan2(y2 - y1, x2 - x1);
            Polygon p = new Polygon();
            p.addPoint(x2, y2);
            p.addPoint((int) (x2 - 9 * Math.cos(a - 0.4)), (int) (y2 - 9 * Math.sin(a - 0.4)));
            p.addPoint((int) (x2 - 9 * Math.cos(a + 0.4)), (int) (y2 - 9 * Math.sin(a + 0.4)));
            g.fillPolygon(p);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
            // scale every default Swing font by FONT_SCALE
            for (Object key : java.util.Collections.list(UIManager.getDefaults().keys())) {
                Object val = UIManager.get(key);
                if (val instanceof javax.swing.plaf.FontUIResource) {
                    Font f = (Font) val;
                    UIManager.put(key, new javax.swing.plaf.FontUIResource(f.deriveFont(f.getSize2D() * FONT_SCALE)));
                }
            }
            new TrussGUI().setVisible(true);
        });
    }
}

// ======================================================================
//  TrussEngine - runs the Truss-Solver classes (Nodes, Beam, Force, List,
//  Stack, GaussElim) exactly the way Main.java does, wrapped in a method
//  so the GUI can call it.
// ======================================================================
class TrussEngine {

    static final int MAX_NODES = 52;

    static class Result {
        boolean ok;
        String message = "";
        String[] beamName;
        double[] beamForce;
        double[] rx, ry;
        double residual;
        StringBuilder log = new StringBuilder();
    }

    static char nameOf(int i) { return (char) (i < 26 ? 'A' + i : 'a' + (i - 26)); }
    static int indexOfName(char c) { return c >= 'a' ? 26 + (c - 'a') : c - 'A'; }

    static Result solve(double[] xs, double[] ys, double[] fx, double[] fy,
                        int[] sup, int[][] mem, String unit) {
        Result r = new Result();
        int n = xs.length, m = mem.length;
        r.log.append("Truss-Solver: method of joints (Nodes / Beam / Force / List / Stack / GaussElim)\n");
        r.log.append("Units: forces in ").append(unit).append(", + = up / right, tension +\n\n");

        // ---------- validation ----------
        if (n < 3 || m < 3) { r.message = "Add at least 3 nodes and 3 members."; return r; }
        int pin = -1, roller = -1, pins = 0, rollers = 0;
        for (int i = 0; i < n; i++) {
            if (sup[i] == TrussGUI.PIN) { pin = i; pins++; }
            if (sup[i] == TrussGUI.ROLLER) { roller = i; rollers++; }
        }
        if (pins != 1 || rollers != 1) {
            r.message = "The solver needs exactly one pin support and one roller support (found "
                    + pins + " pin, " + rollers + " roller).";
            return r;
        }
        if (Math.abs(xs[pin] - xs[roller]) < 1e-9) {
            r.message = "The pin and roller are at the same X position - the moment balance cannot be solved.";
            return r;
        }
        int[] degree = new int[n];
        for (int[] e : mem) { degree[e[0]]++; degree[e[1]]++; }
        for (int i = 0; i < n; i++)
            if (degree[i] == 0) { r.message = "Node " + nameOf(i) + " has no members attached."; return r; }
        for (int[] e : mem)
            if (Math.hypot(xs[e[0]] - xs[e[1]], ys[e[0]] - ys[e[1]]) < 1e-9) {
                r.message = "Members " + nameOf(e[0]) + nameOf(e[1]) + " has zero length (nodes overlap).";
                return r;
            }
        if (m + 3 != 2 * n) {
            r.message = String.format("Not statically determinate: members + reactions = %d + 3 = %d, but 2 \u00D7 joints = %d.%s",
                    m, m + 3, 2 * n,
                    (m + 3 < 2 * n) ? " (Too few members - the truss is a mechanism.)"
                                    : " (Too many members - the truss is indeterminate.)");
            return r;
        }

        // ---------- build the repo's objects ----------
        Nodes[] nodes = new Nodes[n];
        for (int i = 0; i < n; i++) nodes[i] = new Nodes(nameOf(i), xs[i], ys[i], sup[i] != TrussGUI.NONE);
        Beam[] beams = new Beam[m];
        for (int j = 0; j < m; j++) beams[j] = new Beam(nodes[mem[j][0]], nodes[mem[j][1]]);

        // ---------- 1. support reactions (moment balance about the pin) ----------
        List<Force> applied = new List<>();
        for (int i = 0; i < n; i++)
            if (Math.abs(fy[i]) > 0 || Math.abs(fx[i]) > 0) applied.append(new Force(nodes[i], fy[i], unit));

        double xp = nodes[pin].getX(), yp = nodes[pin].getY(), xr = nodes[roller].getX();
        double sumFy = 0, sumFx = 0, moment = 0;
        for (int k = 0; k < applied.size(); k++) {
            Force f = applied.getValueAt(k);
            Nodes at = f.getNode();
            int idx = indexOfName(at.getName());
            sumFy += f.getForce();
            sumFx += fx[idx];
            moment += f.getForce() * (at.getX() - xp) - fx[idx] * (at.getY() - yp);
        }
        double rollerRy = -moment / (xr - xp);
        double pinRy = -sumFy - rollerRy;
        double pinRx = -sumFx;

        r.rx = new double[n]; r.ry = new double[n];
        r.rx[pin] = pinRx; r.ry[pin] = pinRy; r.ry[roller] = rollerRy;
        r.log.append(String.format("Reactions:%n  %c (pin)    Rx = %.4f   Ry = %.4f%n  %c (roller) Ry = %.4f%n%n",
                nameOf(pin), pinRx, pinRy, nameOf(roller), rollerRy));

        // total external force at each joint (applied load + reaction)
        double[] extX = new double[n];
        for (int i = 0; i < n; i++) {
            extX[i] = fx[i] + r.rx[i];
            nodes[i].setNodeForce(fy[i] + r.ry[i]);
        }

        // ---------- 2. method of joints ----------
        r.log.append("Joint-by-joint solution:\n");
        int solved = 0;
        boolean progress = true;
        while (progress && solved < m) {
            progress = false;
            Stack<Nodes> stack = new Stack<>();
            for (int i = n - 1; i >= 0; i--) stack.push(nodes[i]);

            while (!stack.isEmpty()) {
                Nodes node = stack.pop();
                int idx = indexOfName(node.getName());
                List<Beam> conn = node.getConnections();

                int[] unknown = new int[conn.size()];
                int k = 0;
                for (int b = 0; b < conn.size(); b++)
                    if (conn.getValueAt(b).getForce() == null) unknown[k++] = b;
                if (k == 0 || k > 2) continue;

                double rhsX = -extX[idx];
                double rhsY = -node.getNodeForce();
                for (int b = 0; b < conn.size(); b++) {
                    Beam cur = conn.getValueAt(b);
                    if (cur.getForce() == null) continue;
                    cur.setView(node);
                    rhsX -= cur.getForce() * cur.getDX() / cur.getHYP();
                    rhsY -= cur.getForce() * cur.getDY() / cur.getHYP();
                }

                if (k == 1) {
                    Beam t = conn.getValueAt(unknown[0]);
                    t.setView(node);
                    double cx = t.getDX() / t.getHYP(), cy = t.getDY() / t.getHYP();
                    double val = Math.abs(cx) >= Math.abs(cy) ? rhsX / cx : rhsY / cy;
                    t.setForce(val);
                    solved++; progress = true;
                    r.log.append(String.format("  Joint %c: %s = %.4f%n", node.getName(), t.getName(), val));
                } else {
                    Beam b1 = conn.getValueAt(unknown[0]), b2 = conn.getValueAt(unknown[1]);
                    b1.setView(node); b2.setView(node);
                    double c1x = b1.getDX() / b1.getHYP(), c1y = b1.getDY() / b1.getHYP();
                    double c2x = b2.getDX() / b2.getHYP(), c2y = b2.getDY() / b2.getHYP();
                    if (Math.abs(c1x * c2y - c2x * c1y) < 1e-9) continue;   // collinear unknowns: wait
                    double[][] variables = {{c1x, c2x}, {c1y, c2y}};
                    double[] answers = {rhsX, rhsY};
                    double[] ans = new GaussElim(variables, answers).getValues();
                    b1.setForce(ans[0]); b2.setForce(ans[1]);
                    solved += 2; progress = true;
                    r.log.append(String.format("  Joint %c: %s = %.4f, %s = %.4f%n",
                            node.getName(), b1.getName(), ans[0], b2.getName(), ans[1]));
                }
            }
        }

        if (solved < m) {
            StringBuilder s = new StringBuilder();
            for (Beam b : beams) if (b.getForce() == null) s.append(b.getName()).append(' ');
            r.message = "Could not resolve member(s): " + s.toString().trim()
                    + ". No joint had \u2264 2 unknown members with independent directions - the truss is probably unstable or "
                    + "needs the method of sections.";
            r.log.append("\n").append(r.message).append('\n');
            return r;
        }

        // ---------- 3. equilibrium check at every joint ----------
        double scale = 1, resid = 0;
        for (int i = 0; i < n; i++) scale = Math.max(scale, Math.max(Math.abs(fy[i]) + Math.abs(r.ry[i]), Math.abs(fx[i]) + Math.abs(r.rx[i])));
        for (int i = 0; i < n; i++) {
            double sx = extX[i], sy = nodes[i].getNodeForce();
            List<Beam> conn = nodes[i].getConnections();
            for (int b = 0; b < conn.size(); b++) {
                Beam cur = conn.getValueAt(b);
                cur.setView(nodes[i]);
                sx += cur.getForce() * cur.getDX() / cur.getHYP();
                sy += cur.getForce() * cur.getDY() / cur.getHYP();
            }
            resid = Math.max(resid, Math.max(Math.abs(sx), Math.abs(sy)));
        }
        r.residual = resid;
        r.log.append(String.format("%nMax joint residual: %.3e%n", resid));
        if (resid > 1e-6 * scale) {
            r.message = String.format("Equilibrium check failed (residual %.3g) - the truss geometry is unstable "
                    + "(for example a joint whose members are all collinear with an unbalanced load).", resid);
            r.log.append(r.message).append('\n');
            return r;
        }

        r.beamName = new String[m];
        r.beamForce = new double[m];
        for (int j = 0; j < m; j++) { r.beamName[j] = beams[j].getName(); r.beamForce[j] = beams[j].getForce(); }
        r.ok = true;
        r.message = "Solved.";
        return r;
    }
}
