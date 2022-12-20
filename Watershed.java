import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class Watershed {

  // Método que aplica o algoritmo de Watershed na imagem dada
  public static BufferedImage segmentImage(BufferedImage image) {
    // Transforma a imagem em escala de cinza
    ColorConvertOp op = new ColorConvertOp(image.getColorModel().getColorSpace(), ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    BufferedImage grayImage = op.filter(image, null);

    // Aplica o filtro de média na imagem
    float[] kernelData = {
      1/9f, 1/9f, 1/9f,
      1/9f, 1/9f, 1/9f,
      1/9f, 1/9f, 1/9f
    };
    Kernel kernel = new Kernel(3, 3, kernelData);
    ConvolveOp convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
    BufferedImage smoothImage = convolveOp.filter(grayImage, null);

    // Aplica o operador de Sobel na imagem
    float[] sobelData = {
      -1, 0, 1,
      -2, 0, 2,
      -1, 0, 1
    };
    kernel = new Kernel(3, 3, sobelData);
    convolveOp = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
    BufferedImage sobelImage = convolveOp.filter(smoothImage, null);

    // Cria uma imagem binária a partir da imagem transformada
    int threshold = 128;
    BufferedImage binaryImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int pixel = sobelImage.getRGB(x, y) & 0xff;
        if (pixel > threshold) {
          binaryImage.setRGB(x, y, 0xffffff);
        } else {
          binaryImage.setRGB(x, y, 0);
        }
      }
    }

    // Cria a matriz de rótulos
    int[][] labels = new int[image.getHeight()][image.getWidth()];
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        labels[y][x] = -1;
      }
    }

    // Atribui rótulos aos pixels da imagem binária
    int nextLabel = 0
