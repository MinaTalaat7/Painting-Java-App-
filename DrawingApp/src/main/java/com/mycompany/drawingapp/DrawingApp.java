/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.drawingapp;

/**
 *
 * @author Creative
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DrawingApp extends JFrame {

    private JButton redButton, greenButton, blueButton, rectangleButton, ovalButton, lineButton,
            freeHandButton, eraserButton, clearAllButton, undoButton, saveButton, openButton;
    private JCheckBox dottedCheckbox, filledCheckbox;
    private Color currentColor = Color.BLACK;
    private int currentShape = 0; // 0: Freehand, 1: Rectangle, 2: Oval, 3: Line
    private boolean isDotted = false;
    private boolean isFilled = false;
    private boolean isErasing = false;
    private boolean isDragging = false;
    private int startX, startY, endX, endY;
    private BufferedImage drawingImage;
    private Graphics2D g2d;
    private java.util.List<ShapeDrawn> shapesDrawn = new java.util.ArrayList<>();
    private java.util.List<ShapeDrawn> undoneShapes = new java.util.ArrayList<>();
    private java.util.List<Point> freeHandPoints = new java.util.ArrayList<>();

    public DrawingApp() {
        setTitle("DrawingApp");
        setSize(1600, 1600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
        setupListeners();
        setupDrawingArea();

        setVisible(true);
    }

    private void initializeComponents() {
        redButton = new JButton("Red");
        greenButton = new JButton("Green");
        blueButton = new JButton("Blue");
        rectangleButton = new JButton("Rectangle");
        ovalButton = new JButton("Oval");
        lineButton = new JButton("Line");
        freeHandButton = new JButton("Free Hand");
        eraserButton = new JButton("Eraser");
        clearAllButton = new JButton("Clear All");
        undoButton = new JButton("Undo");
        saveButton = new JButton("Save");
        openButton = new JButton("Open");

        dottedCheckbox = new JCheckBox("Dotted");
        filledCheckbox = new JCheckBox("Filled");

        JPanel controlPanel = new JPanel();
        controlPanel.add(redButton);
        controlPanel.add(greenButton);
        controlPanel.add(blueButton);
        controlPanel.add(rectangleButton);
        controlPanel.add(ovalButton);
        controlPanel.add(lineButton);
        controlPanel.add(freeHandButton);
        controlPanel.add(eraserButton);
        controlPanel.add(clearAllButton);
        controlPanel.add(undoButton);
        controlPanel.add(saveButton);
        controlPanel.add(openButton);
        controlPanel.add(dottedCheckbox);
        controlPanel.add(filledCheckbox);

        add(controlPanel, BorderLayout.NORTH);
    }

    private void setupListeners() {
        redButton.addActionListener(e -> setCurrentColor(Color.RED));
        greenButton.addActionListener(e -> setCurrentColor(Color.GREEN));
        blueButton.addActionListener(e -> setCurrentColor(Color.BLUE));
        rectangleButton.addActionListener(e -> setCurrentShape(1));
        ovalButton.addActionListener(e -> setCurrentShape(2));
        lineButton.addActionListener(e -> setCurrentShape(3));
        freeHandButton.addActionListener(e -> setCurrentShape(0));
        eraserButton.addActionListener(e -> setEraserMode());
        clearAllButton.addActionListener(e -> clearDrawingArea());
        undoButton.addActionListener(e -> undo());
        saveButton.addActionListener(e -> saveDrawing());
        openButton.addActionListener(e -> openImage());

        dottedCheckbox.addActionListener(e -> isDotted = dottedCheckbox.isSelected());
        filledCheckbox.addActionListener(e -> isFilled = filledCheckbox.isSelected());
    }

    private void setupDrawingArea() {
        drawingImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        g2d = (Graphics2D) drawingImage.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                isDragging = true;
                if (currentShape == 0) {
                    freeHandPoints.clear();
                    freeHandPoints.add(new Point(startX, startY));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                isDragging = false;

                if (!isErasing) {
                    if (currentShape == 0) {
                        drawFreeHand();
                    } else {
                        drawShape();
                    }
                } else {
                    erase();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    endX = e.getX();
                    endY = e.getY();
                    repaint();
                    if (currentShape == 0) {
                        freeHandPoints.add(new Point(endX, endY));
                    }
                }
            }
        });

        getContentPane().add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(drawingImage, 0, 0, null);
                if (isDragging && !isErasing) {
                    if (currentShape == 0) {
                        drawFreeHandPreview((Graphics2D) g);
                    } else {
                        drawShapePreview((Graphics2D) g);
                    }
                }
            }
        }, BorderLayout.CENTER);
    }

    private void setCurrentColor(Color color) {
        currentColor = color;
        isErasing = false;
    }

    private void setCurrentShape(int shape) {
        currentShape = shape;
        isErasing = false;
    }

    private void setEraserMode() {
        setCurrentColor(getBackground());
        isErasing = true;
    }

    private void drawShape() {
        g2d.setColor(currentColor);
        if (isFilled) {
            g2d.fillRect(startX, startY, endX - startX, endY - startY);
            
        } else {
            if (isDotted) {
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
            } else {
                g2d.setStroke(new BasicStroke(2));
            }

            switch (currentShape) {
                case 1: // Rectangle
                    g2d.drawRect(startX, startY, endX - startX, endY - startY);
                    break;
                case 2: // Oval
                    g2d.drawOval(startX, startY, endX - startX, endY - startY);
                    break;
                case 3: // Line
                    g2d.drawLine(startX, startY, endX, endY);
                    break;
            }
        }
        shapesDrawn.add(new ShapeDrawn(currentColor, currentShape, isFilled, isDotted, startX, startY, endX, endY));
        repaint();
    }

    private void erase() {
        g2d.setColor(getBackground());
        g2d.fillRect(startX, startY, endX - startX, endY - startY);
        shapesDrawn.add(new ShapeDrawn(currentColor, currentShape, isFilled, isDotted, startX, startY, endX, endY));
        repaint();
    }

    private void drawShapePreview(Graphics2D g) {
        g.setColor(currentColor);
        if (isFilled) {
            g.fillRect(startX, startY, endX - startX, endY - startY);
        } else {
            if (isDotted) {
                g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
            } else {
                g.setStroke(new BasicStroke(2));
            }

            switch (currentShape) {
                case 1: // Rectangle
                    g.drawRect(startX, startY, endX - startX, endY - startY);
                    break;
                case 2: // Oval
                    g.drawOval(startX, startY, endX - startX, endY - startY);
                    break;
                case 3: // Line
                    g.drawLine(startX, startY, endX, endY);
                    break;
            }
        }
    }

    private void drawFreeHand() {
        g2d.setColor(currentColor);
        if (isDotted) {
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
        } else {
            g2d.setStroke(new BasicStroke(2));
        }

        for (int i = 0; i < freeHandPoints.size() - 1; i++) {
            Point p1 = freeHandPoints.get(i);
            Point p2 = freeHandPoints.get(i + 1);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        shapesDrawn.add(new ShapeDrawn(currentColor, currentShape, isFilled, isDotted,
                startX, startY, endX, endY, new java.util.ArrayList<>(freeHandPoints)));
        freeHandPoints.clear();
        repaint();
    }

    private void drawFreeHandPreview(Graphics2D g) {
        g.setColor(currentColor);
        if (isDotted) {
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
        } else {
            g.setStroke(new BasicStroke(2));
        }

        for (int i = 0; i < freeHandPoints.size() - 1; i++) {
            Point p1 = freeHandPoints.get(i);
            Point p2 = freeHandPoints.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void clearDrawingArea() {
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        shapesDrawn.clear();
        undoneShapes.clear();
        repaint();
    }

    private void undo() {
        if (!shapesDrawn.isEmpty()) {
            ShapeDrawn lastShape = shapesDrawn.remove(shapesDrawn.size() - 1);
            undoneShapes.add(lastShape);
            clearDrawingArea();
            for (ShapeDrawn shape : shapesDrawn) {
                shape.draw(g2d);
            }
            repaint();
        }
    }

    private void redo() {
        if (!undoneShapes.isEmpty()) {
            ShapeDrawn redoShape = undoneShapes.remove(undoneShapes.size() - 1);
            shapesDrawn.add(redoShape);
            redoShape.draw(g2d);
            repaint();
        }
    }

    private void saveDrawing() {
        try {
            ImageIO.write(drawingImage, "png", new File("drawing.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                BufferedImage image = ImageIO.read(selectedFile);
                g2d.drawImage(image, 0, 0, null);
                repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ShapeDrawn {
        private Color color;
        private int shape;
        private boolean filled;
        private boolean dotted;
        private int startX, startY, endX, endY;
        private java.util.List<Point> freeHandPoints;

        public ShapeDrawn(Color color, int shape, boolean filled, boolean dotted,
                          int startX, int startY, int endX, int endY) {
            this.color = color;
            this.shape = shape;
            this.filled = filled;
            this.dotted = dotted;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.freeHandPoints = new java.util.ArrayList<>();
        }

        public ShapeDrawn(Color color, int shape, boolean filled, boolean dotted,
                          int startX, int startY, int endX, int endY, java.util.List<Point> freeHandPoints) {
            this(color, shape, filled, dotted, startX, startY, endX, endY);
            this.freeHandPoints.addAll(freeHandPoints);
        }

        public void draw(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);

            if (filled) {
                g2d.fillRect(startX, startY, endX - startX, endY - startY);
            } else {
                if (dotted) {
                    g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
                } else {
                    g2d.setStroke(new BasicStroke(2));
                }

                switch (shape) {
                    case 1: // Rectangle
                        g2d.drawRect(startX, startY, endX - startX, endY - startY);
                        break;
                    case 2: // Oval
                        g2d.drawOval(startX, startY, endX - startX, endY - startY);
                        break;
                    case 3: // Line
                        g2d.drawLine(startX, startY, endX, endY);
                        break;
                    case 0: // Freehand
                        for (int i = 0; i < freeHandPoints.size() - 1; i++) {
                            Point p1 = freeHandPoints.get(i);
                            Point p2 = freeHandPoints.get(i + 1);
                            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                        break;
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DrawingApp());
    }
}
