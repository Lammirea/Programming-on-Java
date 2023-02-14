import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Function;

public class Form_Integral extends JFrame{
    private JButton addbtn = new JButton("Добавить"),
            delbtn = new JButton("Удалить"),
            countbtn = new JButton("Вычислить");

    private JTextField highText = new JTextField("",10),
            lowText = new JTextField("",10),
            StepText = new JTextField("",10);

    private JLabel highlabel = new JLabel("Введите верхнюю границу: "),
            lowlabel = new JLabel("Введите нижнюю границу: "),
            steplabel = new JLabel("Введите шаг интегрирования: ");

    private JTable table;
    // JFrame
    static JFrame f;

    //Создаём отоброжаемую на экране форму
    public Form_Integral() {
        setTitle("Вычисление интеграла");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100,100,550,450);
        Container c = getContentPane();

        JPanel panel = new JPanel(),
                btnpanel = new JPanel();
        JScrollPane tablePanel = new JScrollPane();

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(highlabel)
                                .addComponent(lowlabel)
                                .addComponent(steplabel)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(highText)
                                .addComponent(lowText)
                                .addComponent(StepText)
                        )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(highlabel)
                                .addComponent(highText))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lowlabel)
                                .addComponent(lowText))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(steplabel)
                                .addComponent(StepText))
        );

        btnpanel.setLayout(new BoxLayout(btnpanel,BoxLayout.X_AXIS));
        //Панель с кнопками
        countbtn.addActionListener(new BtnEventListener());
        addbtn.addActionListener(new AddDataBtn());
        delbtn.addActionListener(new DeleteDataBtn());
        btnpanel.add(addbtn);
        btnpanel.add(delbtn);
        btnpanel.add(countbtn);
        btnpanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Панель с табличкой
        String[] columnNames = { "Верхняя граница", "Нижняя граница", "Шаг интегрирования", "Результат" };
        int numRows = 0;
        DefaultTableModel model = new DefaultTableModel(numRows,columnNames.length);
        model.setColumnIdentifiers(columnNames);
        table = new JTable(model)
        {
            public boolean isCellEditable(int row, int column) {
                //Блокируем все,кроме четвёртой
                return column != 3;
            }
        };
        table.setSize(530,250);
        table.setBackground(new Color(164,191,220));
        table.addMouseListener(new TableMouseClicked());

        panel.setBackground(new Color(164,191,220));
        //btnpanel.setBackground(Color.blue);
        //tablePanel.setBackground(Color.green);

        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        c.add(panel);
        c.add(btnpanel);

        c.add(new JScrollPane(table));

    }

    //Эта функция выводит на текстовые поля данные из строки таблицы,на которую мы нажали
    class TableMouseClicked implements MouseListener{
        public void mouseClicked(MouseEvent e) {
            DefaultTableModel tblModel = (DefaultTableModel)table.getModel();

            String tblHigh = tblModel.getValueAt(table.getSelectedRow(),0).toString();
            String tblLow = tblModel.getValueAt(table.getSelectedRow(),1).toString();
            String tblStep = tblModel.getValueAt(table.getSelectedRow(),2).toString();

            highText.setText(tblHigh);
            lowText.setText(tblLow);
            StepText.setText(tblStep);
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //В этой функции совершается подсчёт интеграла
    class BtnEventListener extends Component implements ActionListener{
        //Здесь будет выполняться вычисление интеграла
        public void actionPerformed (ActionEvent e){
            double integral = 0;

            DefaultTableModel tblModel = (DefaultTableModel) table.getModel();
            double b = Double.valueOf(tblModel.getValueAt(table.getSelectedRow(),0).toString()),
            a = Double.valueOf(tblModel.getValueAt(table.getSelectedRow(),1).toString()),
            h = Double.valueOf(tblModel.getValueAt(table.getSelectedRow(),2).toString());

            if(h > b || h < 0.0 || a > b){
                JOptionPane.showMessageDialog(null, "Введены некорректные данные!");
            }else {
                Function function;
                for (double i = a; i < b; i += h) {
                    if(b - i > h){
                        integral += h * (0.5 * (Math.tan(i) + Math.tan(i + h)));
                    }
                    else{
                        integral += (b - i) * (0.5 * (Math.tan(i) + Math.tan(i + b)));
                    }
                }

                //Записываем данные в таблицу
                if (table.getSelectedColumnCount() == 1) {
                    String high = highText.getText(),
                            low = lowText.getText(),
                            Step = StepText.getText();

                    tblModel.setValueAt(high, table.getSelectedRow(), 0);
                    tblModel.setValueAt(low, table.getSelectedRow(), 1);
                    tblModel.setValueAt(Step, table.getSelectedRow(), 2);
                    tblModel.setValueAt(String.valueOf(integral), table.getSelectedRow(), 3);

                } else {
                    if (table.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Таблица пустая!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Выберите строку,которую надо вычислить!");
                    }
                }
            }
        }
    }

    //Добавление информации в таблицу
    class AddDataBtn extends Component implements  ActionListener{
        public void actionPerformed (ActionEvent e) {
            if(Double.valueOf(StepText.getText()) > Double.valueOf(highText.getText()) || Double.valueOf(StepText.getText()) < 0.0 || Double.valueOf(lowText.getText())>Double.valueOf(highText.getText())){
                JOptionPane.showMessageDialog(null, "Введены некорректные данные");
            }else {
                if (highText.getText().equals("") || lowText.getText().equals("") || StepText.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Не все поля заполнены!");
                } else {
                    String data[] = {highText.getText(), lowText.getText(), StepText.getText()};

                    DefaultTableModel tblModel = (DefaultTableModel) table.getModel();
                    tblModel.addRow(data);


                    //очищаем поле для новых данных
                    highText.setText("");
                    lowText.setText("");
                    StepText.setText("");
                }
            }
        }
    }

    //Удаление данных из таблицы
    class DeleteDataBtn extends Component implements ActionListener{
        public void actionPerformed(ActionEvent e){
            DefaultTableModel tblModel = (DefaultTableModel)table.getModel();

            if (table.getSelectedColumnCount() == 1){
                tblModel.removeRow(table.getSelectedRow());
            }else{
                if (table.getRowCount() == 0){
                    JOptionPane.showMessageDialog(null, "Таблица пустая!");
                }
                else{
                    //если таблица не пустая, но ничего не выбрано
                    JOptionPane.showMessageDialog(null, "Вы ничего не выбрали!");
                }
            }
        }
    }
}
