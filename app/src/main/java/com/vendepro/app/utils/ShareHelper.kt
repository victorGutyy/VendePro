package com.vendepro.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import com.vendepro.app.data.model.Product
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.ArrayList

object ShareHelper {

    fun filePathToUri(context: Context, path: String): Uri? {
        if (path.isBlank()) return null
        val file = File(path)
        if (!file.exists()) return null
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun generateProductCard(context: Context, product: Product): Uri? {
        val w = 1080
        val h = 1350
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawCard(canvas, product, w, h)
        return saveImage(context, bmp, "product_${product.id}_${System.currentTimeMillis()}")
    }

    fun generateCatalogCard(context: Context, products: List<Product>): Uri? {
        val cols  = 2
        val cellW = 540
        val cellH = 540
        val rows  = ((products.size + 1) / 2).coerceAtMost(4)
        val w     = cols * cellW
        val h     = 120 + rows * cellH + 200
        val bmp   = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawBackground(canvas, w, h)
        drawCatalogHeader(canvas, products.firstOrNull()?.businessName ?: "Catalogo", w)
        products.take(rows * cols).forEachIndexed { index, product ->
            val col  = index % cols
            val row  = index / cols
            drawMiniCard(canvas, product, col * cellW, 120 + row * cellH, cellW, cellH)
        }
        drawCatalogFooter(canvas, products.firstOrNull(), w, h)
        return saveImage(context, bmp, "catalog_${System.currentTimeMillis()}")
    }

    private fun drawCard(canvas: Canvas, p: Product, w: Int, h: Int) {
        val gradPaint = Paint()
        gradPaint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(
                Color.parseColor("#0D0D0D"),
                Color.parseColor("#1A1A2E"),
                Color.parseColor("#16213E")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        canvas.drawPaint(gradPaint)

        val accentPaint = Paint()
        accentPaint.color = Color.parseColor("#FF6B35")
        canvas.drawRect(0f, 0f, 8f, h.toFloat(), accentPaint)

        val headerBgPaint = Paint()
        headerBgPaint.color = Color.parseColor("#FF6B35")
        headerBgPaint.alpha = 30
        canvas.drawRect(0f, 0f, w.toFloat(), 160f, headerBgPaint)

        val businessPaint = Paint()
        businessPaint.color = Color.WHITE
        businessPaint.textSize = 52f
        businessPaint.typeface = Typeface.DEFAULT_BOLD
        businessPaint.isAntiAlias = true
        canvas.drawText(p.businessName.uppercase(), 60f, 100f, businessPaint)

        val datePaint = Paint()
        datePaint.color = Color.parseColor("#FF6B35")
        datePaint.textSize = 28f
        datePaint.isAntiAlias = true
        val dateStr = SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale("es", "CO")).format(Date(p.uploadedAt))
        canvas.drawText(dateStr, 60f, 145f, datePaint)

        val imgTop    = 180f
        val imgBottom = 780f

        if (p.imagePath1.isNotEmpty()) {
            val imageFile = File(p.imagePath1)
            if (imageFile.exists()) {
                val srcBmp = BitmapFactory.decodeFile(p.imagePath1)
                if (srcBmp != null) {
                    val scaled = Bitmap.createScaledBitmap(srcBmp, w, (imgBottom - imgTop).toInt(), true)
                    canvas.drawBitmap(scaled, 0f, imgTop, null)
                }
            }
        } else {
            val placePaint = Paint()
            placePaint.color = Color.parseColor("#1F1F3D")
            canvas.drawRect(0f, imgTop, w.toFloat(), imgBottom, placePaint)
        }

        val overlayPaint = Paint()
        overlayPaint.shader = LinearGradient(
            0f, imgBottom - 200f, 0f, imgBottom,
            intArrayOf(Color.TRANSPARENT, Color.parseColor("#0D0D0D")),
            null,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, imgTop, w.toFloat(), imgBottom, overlayPaint)

        val fmt = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        val pricePaint = Paint()
        pricePaint.color = Color.parseColor("#FF6B35")
        pricePaint.textSize = 72f
        pricePaint.typeface = Typeface.DEFAULT_BOLD
        pricePaint.isAntiAlias = true
        canvas.drawText(fmt.format(p.price), 60f, imgBottom - 20f, pricePaint)

        val namePaint = Paint()
        namePaint.color = Color.WHITE
        namePaint.textSize = 58f
        namePaint.typeface = Typeface.DEFAULT_BOLD
        namePaint.isAntiAlias = true
        canvas.drawText(p.productName, 60f, 860f, namePaint)

        val descPaint = Paint()
        descPaint.color = Color.parseColor("#CCCCCC")
        descPaint.textSize = 32f
        descPaint.isAntiAlias = true
        wrapText(canvas, p.description, 60f, 910f, (w - 120).toFloat(), 36f, descPaint, 3)

        val sellerPaint = Paint()
        sellerPaint.color = Color.parseColor("#FF6B35")
        sellerPaint.textSize = 34f
        sellerPaint.isAntiAlias = true
        canvas.drawText("Vendedor: ${p.sellerName}", 60f, 1060f, sellerPaint)

        val footerBgPaint = Paint()
        footerBgPaint.color = Color.parseColor("#FF6B35")
        footerBgPaint.alpha = 25
        canvas.drawRect(0f, 1120f, w.toFloat(), h.toFloat(), footerBgPaint)

        val linePaint = Paint()
        linePaint.color = Color.parseColor("#FF6B35")
        linePaint.strokeWidth = 2f
        canvas.drawLine(0f, 1125f, w.toFloat(), 1125f, linePaint)

        val footerTitlePaint = Paint()
        footerTitlePaint.color = Color.parseColor("#FF6B35")
        footerTitlePaint.textSize = 30f
        footerTitlePaint.typeface = Typeface.DEFAULT_BOLD
        footerTitlePaint.isAntiAlias = true
        canvas.drawText("CONTACTO Y PAGOS", 60f, 1175f, footerTitlePaint)

        val footerTextPaint = Paint()
        footerTextPaint.color = Color.WHITE
        footerTextPaint.textSize = 34f
        footerTextPaint.isAntiAlias = true
        canvas.drawText("Tel: ${p.contactNumber}", 60f, 1225f, footerTextPaint)
        if (p.nequiAccount.isNotEmpty()) {
            canvas.drawText("Nequi: ${p.nequiAccount}", 60f, 1275f, footerTextPaint)
        }
        if (p.daviplataAccount.isNotEmpty()) {
            canvas.drawText("Daviplata: ${p.daviplataAccount}", 60f, 1325f, footerTextPaint)
        }
    }

    private fun drawMiniCard(canvas: Canvas, p: Product, left: Int, top: Int, w: Int, h: Int) {
        val bgPaint = Paint()
        bgPaint.color = Color.parseColor("#1A1A2E")
        canvas.drawRect(left.toFloat(), top.toFloat(), (left + w).toFloat(), (top + h).toFloat(), bgPaint)

        if (p.imagePath1.isNotEmpty()) {
            val imageFile = File(p.imagePath1)
            if (imageFile.exists()) {
                val srcBmp = BitmapFactory.decodeFile(p.imagePath1)
                if (srcBmp != null) {
                    val scaled = Bitmap.createScaledBitmap(srcBmp, w, h - 120, true)
                    canvas.drawBitmap(scaled, left.toFloat(), top.toFloat(), null)
                }
            }
        }

        val infoBgPaint = Paint()
        infoBgPaint.color = Color.parseColor("#0D0D0D")
        infoBgPaint.alpha = 220
        canvas.drawRect(left.toFloat(), (top + h - 120).toFloat(), (left + w).toFloat(), (top + h).toFloat(), infoBgPaint)

        val namePaint = Paint()
        namePaint.color = Color.WHITE
        namePaint.textSize = 28f
        namePaint.isAntiAlias = true
        canvas.drawText(p.productName, left + 16f, (top + h - 70).toFloat(), namePaint)

        val fmt = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        val pricePaint = Paint()
        pricePaint.color = Color.parseColor("#FF6B35")
        pricePaint.textSize = 32f
        pricePaint.typeface = Typeface.DEFAULT_BOLD
        pricePaint.isAntiAlias = true
        canvas.drawText(fmt.format(p.price), left + 16f, (top + h - 28).toFloat(), pricePaint)
    }

    private fun drawBackground(canvas: Canvas, w: Int, h: Int) {
        val paint = Paint()
        paint.shader = LinearGradient(
            0f, 0f, 0f, h.toFloat(),
            intArrayOf(Color.parseColor("#0D0D0D"), Color.parseColor("#1A1A2E")),
            null,
            Shader.TileMode.CLAMP
        )
        canvas.drawPaint(paint)
    }

    private fun drawCatalogHeader(canvas: Canvas, name: String, w: Int) {
        val bgPaint = Paint()
        bgPaint.color = Color.parseColor("#FF6B35")
        bgPaint.alpha = 40
        canvas.drawRect(0f, 0f, w.toFloat(), 120f, bgPaint)

        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 56f
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(name.uppercase(), w / 2f, 82f, textPaint)
    }

    private fun drawCatalogFooter(canvas: Canvas, p: Product?, w: Int, h: Int) {
        val bgPaint = Paint()
        bgPaint.color = Color.parseColor("#FF6B35")
        bgPaint.alpha = 25
        canvas.drawRect(0f, (h - 200).toFloat(), w.toFloat(), h.toFloat(), bgPaint)

        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 30f
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER

        canvas.drawText("Tel: ${p?.contactNumber ?: ""}", w / 2f, (h - 140).toFloat(), textPaint)
        canvas.drawText(
            "Nequi: ${p?.nequiAccount ?: ""}   Daviplata: ${p?.daviplataAccount ?: ""}",
            w / 2f, (h - 90).toFloat(), textPaint
        )

        val datePaint = Paint()
        datePaint.color = Color.parseColor("#FF6B35")
        datePaint.textSize = 26f
        datePaint.isAntiAlias = true
        datePaint.textAlign = Paint.Align.CENTER
        val dateStr = SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale("es", "CO")).format(Date())
        canvas.drawText(dateStr, w / 2f, (h - 40).toFloat(), datePaint)
    }

    private fun wrapText(
        canvas: Canvas, text: String,
        x: Float, y: Float, maxW: Float, lineH: Float,
        paint: Paint, maxLines: Int
    ) {
        var line  = ""
        var curY  = y
        var count = 0
        for (word in text.split(" ")) {
            val test = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(test) > maxW) {
                canvas.drawText(line, x, curY, paint)
                line  = word
                curY += lineH
                count++
                if (count >= maxLines) break
            } else {
                line = test
            }
        }
        if (line.isNotEmpty() && count < maxLines) {
            canvas.drawText(line, x, curY, paint)
        }
    }

    private fun saveImage(context: Context, bmp: Bitmap, name: String): Uri? {
        return try {
            val dir = context.getExternalFilesDir("Shares")
            if (dir != null) {
                dir.mkdirs()
                val file = File(dir, "$name.jpg")
                val stream = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                stream.flush()
                stream.close()
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareToWhatsApp(context: Context, uri: Uri, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.setPackage("com.whatsapp")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Compartir en WhatsApp"))
    }

    fun shareToInstagram(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.setPackage("com.instagram.android")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Compartir en Instagram"))
    }

    fun shareGeneral(context: Context, uri: Uri, text: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Compartir en..."))
    }

    fun buildProductText(p: Product): String {
        val fmt  = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        val date = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale("es", "CO")).format(Date(p.uploadedAt))
        return "Emprendimiento: ${p.businessName}\n\nProducto: ${p.productName}\n${p.description}\n\nPrecio: ${fmt.format(p.price)}\nVendedor: ${p.sellerName}\nContacto: ${p.contactNumber}\n\nNequi: ${p.nequiAccount}\nDaviplata: ${p.daviplataAccount}\n\nFecha: $date"
    }

    fun shareToWhatsAppPersonal(
        context: Context,
        imageUris: List<Uri>,
        text: String
    ) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imageUris))
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }
}
