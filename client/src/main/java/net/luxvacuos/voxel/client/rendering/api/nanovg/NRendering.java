/*
 * This file is part of Voxel
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.voxel.client.rendering.api.nanovg;

import static net.luxvacuos.voxel.universal.core.GlobalVariables.REGISTRY;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_CCW;
import static org.lwjgl.nanovg.NanoVG.NVG_CW;
import static org.lwjgl.nanovg.NanoVG.NVG_HOLE;
import static org.lwjgl.nanovg.NanoVG.NVG_PI;
import static org.lwjgl.nanovg.NanoVG.nnvgText;
import static org.lwjgl.nanovg.NanoVG.nnvgTextBreakLines;
import static org.lwjgl.nanovg.NanoVG.nvgArc;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgBoxGradient;
import static org.lwjgl.nanovg.NanoVG.nvgClosePath;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgFontBlur;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgImagePattern;
import static org.lwjgl.nanovg.NanoVG.nvgImageSize;
import static org.lwjgl.nanovg.NanoVG.nvgLineTo;
import static org.lwjgl.nanovg.NanoVG.nvgLinearGradient;
import static org.lwjgl.nanovg.NanoVG.nvgMoveTo;
import static org.lwjgl.nanovg.NanoVG.nvgPathWinding;
import static org.lwjgl.nanovg.NanoVG.nvgRect;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;
import static org.lwjgl.nanovg.NanoVG.nvgStroke;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeColor;
import static org.lwjgl.nanovg.NanoVG.nvgStrokeWidth;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;
import static org.lwjgl.nanovg.NanoVG.nvgTextMetrics;
import static org.lwjgl.nanovg.NanoVGGL3.nvglCreateImageFromHandle;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NVGTextRow;

import net.luxvacuos.voxel.client.rendering.api.glfw.Window;
import net.luxvacuos.voxel.client.ui.ScrollPaneElement;

/**
 *
 * Nano UI
 *
 */
public class NRendering {

	public static enum ButtonStyle {
		CLOSE, MAXIMIZE, MINIMIZE, NONE, LEFT_ARROW, RIGHT_ARROW
	};

	public static enum BackgroundStyle {
		SOLID, TRANSPARENT
	};

	public static final NVGPaint paintA = NVGPaint.create();
	public static final NVGPaint paintB = NVGPaint.create();
	public static final NVGPaint paintC = NVGPaint.create();
	public static final NVGColor colorA = NVGColor.create();
	public static final NVGColor colorB = NVGColor.create();
	public static final NVGColor colorC = NVGColor.create();

	public static final ByteBuffer ICON_SEARCH = cpToUTF8(0x1F50D);
	public static final ByteBuffer ICON_CIRCLED_CROSS = cpToUTF8(0x2716);
	public static final ByteBuffer ICON_CHEVRON_RIGHT = cpToUTF8(0xE75E);
	public static final ByteBuffer ICON_CHECK = cpToUTF8(0x2713);
	public static final ByteBuffer ICON_LOGIN = cpToUTF8(0xE740);
	public static final ByteBuffer ICON_TRASH = cpToUTF8(0xE729);
	public static final ByteBuffer ICON_INFORMATION_SOURCE = cpToUTF8(0x2139);
	public static final ByteBuffer ICON_GEAR = cpToUTF8(0x2699);
	public static final ByteBuffer ICON_BLACK_RIGHT_POINTING_TRIANGLE = cpToUTF8(0x25B6);

	private static final FloatBuffer lineh = BufferUtils.createFloatBuffer(1);
	private static final NVGTextRow.Buffer rows = NVGTextRow.create(3);

	public static NVGColor rgba(float r, float g, float b, float a, NVGColor color) {
		color.r(r);
		color.g(g);
		color.b(b);
		color.a(a);
		return color;
	}

	public static NVGColor rgba(int r, int g, int b, int a, NVGColor color) {
		color.r(r / 255.0f);
		color.g(g / 255.0f);
		color.b(b / 255.0f);
		color.a(a / 255.0f);
		return color;
	}

	public static NVGColor rgba(int r, int g, int b, int a) {
		NVGColor color = NVGColor.create();
		color.r(r / 255.0f);
		color.g(g / 255.0f);
		color.b(b / 255.0f);
		color.a(a / 255.0f);
		return color;
	}

	public static void renderTitleBarText(long vg, String text, String font, int align, float x, float y,
			float fontSize) {

		nvgSave(vg);
		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);

		nvgFontBlur(vg, 4);
		nvgFillColor(vg, rgba(0, 0, 0, 255, colorA));
		nvgText(vg, x, y + 1, text);

		nvgFontBlur(vg, 0);
		nvgFillColor(vg, rgba(255, 255, 255, 255, colorA));
		nvgText(vg, x, y, text);
		nvgRestore(vg);
	}

	public static void renderWindow(long vg, float x, float y, float w, float h, BackgroundStyle backgroundStyle,
			NVGColor backgroundColor, boolean decorations, boolean titleBar) {
		NVGPaint shadowPaint = paintA;
		float borderSize = (float) REGISTRY.getRegistryItem("/Voxel/Settings/WindowManager/borderSize");
		float titleBarHeight = (float) REGISTRY.getRegistryItem("/Voxel/Settings/WindowManager/titleBarHeight");

		nvgSave(vg);
		if (decorations) {
			// Window
			nvgBeginPath(vg);
			nvgRect(vg, x, y, w, h);
			nvgPathWinding(vg, NVG_HOLE);
			if (titleBar)
				nvgRect(vg, x - borderSize, y - titleBarHeight, w + borderSize * 2f, h + titleBarHeight + borderSize);
			else
				nvgRect(vg, x - borderSize, y - borderSize, w + borderSize * 2f, h + borderSize * 2f);
			nvgFillColor(vg, rgba(31, 31, 31, 120, colorA));
			nvgFill(vg);
		}

		// Background
		switch (backgroundStyle) {
		case SOLID:
			nvgBeginPath(vg);
			nvgRect(vg, x, y, w, h);
			nvgFillColor(vg, backgroundColor);
			nvgFill(vg);
			break;
		case TRANSPARENT:
			break;
		}
		if (decorations) {
			// Drop shadow
			if (titleBar) {
				nvgBoxGradient(vg, x - borderSize, y + 10 - titleBarHeight, w + borderSize * 2f,
						h + titleBarHeight + borderSize, 0, 20, rgba(0, 0, 0, 80, colorA), rgba(0, 0, 0, 0, colorB),
						shadowPaint);
				nvgBeginPath(vg);
				nvgRect(vg, x - 10 - borderSize, y - 10 - titleBarHeight, w + 20 + borderSize * 2f,
						h + 30 + titleBarHeight + borderSize);
				nvgRect(vg, x - borderSize, y - titleBarHeight, w + borderSize * 2f, h + titleBarHeight + borderSize);
			} else {
				nvgBoxGradient(vg, x - borderSize, y + 10 - borderSize, w + borderSize * 2f, h + borderSize * 2f, 0, 20,
						rgba(0, 0, 0, 80, colorA), rgba(0, 0, 0, 0, colorB), shadowPaint);
				nvgBeginPath(vg);
				nvgRect(vg, x - 10 - borderSize, y - 10 - borderSize, w + 20 + borderSize * 2f,
						h + 30 + borderSize * 2f);
				nvgRect(vg, x - borderSize, y - titleBarHeight, w + borderSize * 2f, h + titleBarHeight + borderSize);
			}
			nvgPathWinding(vg, NVG_HOLE);
			nvgFillPaint(vg, shadowPaint);
			nvgFill(vg);

		}

		nvgRestore(vg);
	}

	public static void renderWindowButton(long vg, float x, float y, float w, float h, NVGColor color,
			ButtonStyle style, boolean highlight, NVGColor highlightColor) {
		nvgSave(vg);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		if (highlight)
			nvgFillColor(vg, highlightColor);
		else
			nvgFillColor(vg, color);
		nvgFill(vg);

		switch (style) {
		case CLOSE:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2 - 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2 + 6);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2 - 6);
			nvgStrokeColor(vg, rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case MAXIMIZE:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2 - 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2 - 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 - 6, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 - 6, y + h / 2 - 6);
			nvgStrokeColor(vg, rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case MINIMIZE:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2);
			nvgStrokeColor(vg, rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case LEFT_ARROW:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 - 6, y + h / 2);
			nvgLineTo(vg, x + w / 2, y + h / 2 - 6);
			nvgMoveTo(vg, x + w / 2 - 6, y + h / 2);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2);
			nvgStrokeColor(vg, rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case RIGHT_ARROW:
			nvgBeginPath(vg);
			nvgMoveTo(vg, x + w / 2, y + h / 2 + 6);
			nvgLineTo(vg, x + w / 2 + 6, y + h / 2);
			nvgLineTo(vg, x + w / 2, y + h / 2 - 6);
			nvgMoveTo(vg, x + w / 2 + 6, y + h / 2);
			nvgLineTo(vg, x + w / 2 - 6, y + h / 2);
			nvgStrokeColor(vg, rgba(0, 0, 0, 255, colorA));
			nvgStroke(vg);
			break;
		case NONE:
			break;
		}
		nvgRestore(vg);
	}

	public static void renderText(long vg, String text, String font, int align, float x, float y, float fontSize,
			NVGColor color) {
		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, align);
		nvgFillColor(vg, color);
		nvgText(vg, x, y, text);
	}

	public static void renderImage(long vg, float x, float y, float w, float h, int image, float alpha) {
		NVGPaint imgPaint = paintB;
		nvgSave(vg);
		nvgImagePattern(vg, x, y, w, h, 0, image, alpha, imgPaint);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		nvgFillPaint(vg, imgPaint);
		nvgFill(vg);
		nvgRestore(vg);
	}

	public static void renderImage(long vg, float x, float y, int image, float alpha) {
		NVGPaint imgPaint = paintB;
		IntBuffer imgw = memAllocInt(1), imgh = memAllocInt(1);
		nvgSave(vg);
		nvgImageSize(vg, image, imgw, imgh);
		nvgImagePattern(vg, x, y, imgw.get(0), imgh.get(0), 0, image, alpha, imgPaint);
		nvgBeginPath(vg);
		nvgRect(vg, x, y, imgw.get(0), imgh.get(0));
		nvgFillPaint(vg, imgPaint);
		nvgFill(vg);
		nvgRestore(vg);
		memFree(imgh);
		memFree(imgw);
	}

	public static void renderEditBoxBase(long vg, float x, float y, float w, float h, boolean selected) {
		nvgSave(vg);
		nvgBeginPath(vg);
		nvgRect(vg, x + 1, y + 1, w - 2, h - 2);
		if (selected)
			nvgFillColor(vg, rgba(255, 255, 255, 255, colorA));
		else
			nvgFillColor(vg, rgba(150, 150, 150, 255, colorA));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1);
		if (selected)
			nvgStrokeColor(vg, rgba(50, 50, 50, 255, colorA));
		else
			nvgStrokeColor(vg, rgba(70, 70, 70, 255, colorA));
		nvgStrokeWidth(vg, 1);
		nvgStroke(vg);
		nvgRestore(vg);
	}

	public static void renderEditBox(long vg, String text, String font, float x, float y, float w, float h,
			float fontSize, boolean selected) {
		renderEditBoxBase(vg, x, y, w, h, selected);
		nvgSave(vg);
		nvgScissor(vg, x, y, w, h);
		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		nvgFillColor(vg, rgba(0, 0, 0, 255, colorA));
		nvgText(vg, x + h * 0.3f, y + h * 0.5f, text);
		nvgFillColor(vg, rgba(255, 255, 255, 100, colorA));
		nvgText(vg, x + h * 0.3f, y + h * 0.5f, text);
		nvgRestore(vg);
	}

	public static void renderButton(long vg, ByteBuffer preicon, String text, String font, String entypo, float x,
			float y, float w, float h, NVGColor color, boolean highlight, float fontSize, NVGColor highlightColor) {
		float tw, iw = 0;
		nvgSave(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 1, y + 1, w - 2, h - 2);
		if (highlight)
			nvgFillColor(vg, highlightColor);
		else
			nvgFillColor(vg, color);
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 0.5f, y + 0.5f, w - 1, h - 1);
		nvgStrokeColor(vg, rgba(0, 0, 0, 100, colorA));
		nvgStroke(vg);

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		tw = nvgTextBounds(vg, 0, 0, text, (FloatBuffer) null);
		if (preicon != null) {
			nvgFontSize(vg, h * 1.3f);
			nvgFontFace(vg, entypo);
			iw = nvgTextBounds(vg, 0, 0, preicon, (FloatBuffer) null);
			iw += h * 0.15f;
		}

		if (preicon != null) {
			nvgFontSize(vg, h * 1.3f);
			nvgFontFace(vg, entypo);
			nvgFillColor(vg, rgba(100, 100, 100, 96, colorA));
			nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
			nvgText(vg, x + w * 0.5f - tw * 0.5f - iw * 0.75f, y + h * 0.5f, preicon);
		}

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		nvgFillColor(vg, rgba(60, 60, 60, 255, colorA));
		nvgText(vg, x + w * 0.5f - tw * 0.5f + iw * 0.25f, y + h * 0.5f, text);
		nvgRestore(vg);
	}

	public static void renderScrollPane(long vg, float x, float y, float w, float h, float t, int hSize, float cardW,
			float cardH, List<ScrollPaneElement> elements, Window window) {
		float stackh = (elements.size() / hSize) * (cardH + 10) + 10;
		int i;
		float u = t;
		float scrollh;

		nvgSave(vg);

		// Window
		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		nvgFillColor(vg, rgba(200, 200, 200, 255, colorA));
		nvgFill(vg);

		nvgSave(vg);
		nvgScissor(vg, x, y, w, h);

		for (i = 0; i < elements.size(); i++) {
			float tx, ty;
			tx = x + 10;
			ty = y + 10;
			tx += (i % hSize) * (cardW + 10);
			ty += (i / hSize) * (cardH + 10) - (stackh - h) * u;

			nvgBeginPath(vg);
			nvgRect(vg, tx + 0.5f, ty + 0.5f, cardW - 1, cardH - 1);
			nvgFillColor(vg, rgba(150, 150, 150, 255, colorA));
			nvgFill(vg);
			nvgStrokeWidth(vg, 1.0f);
			nvgStrokeColor(vg, rgba(64, 64, 64, 255, colorA));
			nvgStroke(vg);
			ScrollPaneElement e = elements.get(i);
			e.setX(tx);
			e.setY(window.getHeight() - ty - cardH);
			e.render(window);
		}
		nvgRestore(vg);

		// Scroll bar
		nvgBeginPath(vg);
		nvgRect(vg, x + w - 14, y + 14, 14, h - 28);
		nvgFillColor(vg, rgba(0, 0, 0, 64, colorB));
		nvgFill(vg);

		scrollh = (h / stackh) * (h - 8);
		nvgBeginPath(vg);
		nvgRect(vg, x + w - 14, y + 14 + (h - 8 - scrollh) * u, 14, scrollh - 20);
		nvgFillColor(vg, rgba(220, 220, 220, 255, colorB));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + w - 14, y, 14, 14);
		nvgFillColor(vg, rgba(0, 0, 0, 64, colorB));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgMoveTo(vg, x + w - 12, y + 10);
		nvgLineTo(vg, x + w - 7, y + 3);
		nvgLineTo(vg, x + w - 2, y + 10);
		nvgStrokeColor(vg, rgba(0, 0, 0, 200, colorA));
		nvgStroke(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + w - 14, y + h - 14, 14, 14);
		nvgFillColor(vg, rgba(0, 0, 0, 64, colorB));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgMoveTo(vg, x + w - 12, y + h - 10);
		nvgLineTo(vg, x + w - 7, y + h - 3);
		nvgLineTo(vg, x + w - 2, y + h - 10);
		nvgStrokeColor(vg, rgba(0, 0, 0, 200, colorA));
		nvgStroke(vg);

		nvgRestore(vg);
	}

	public static void renderSpinner(long vg, float cx, float cy, float r, float t) {
		float a0 = 0.0f + t * 6;
		float a1 = NVG_PI + t * 6;
		float r0 = r;
		float r1 = r * 0.75f;
		float ax, ay, bx, by;
		NVGPaint paint = paintA;

		nvgSave(vg);
		nvgBeginPath(vg);
		nvgArc(vg, cx, cy, r0, a0, a1, NVG_CW);
		nvgArc(vg, cx, cy, r1, a1, a0, NVG_CCW);
		nvgClosePath(vg);
		ax = cx + (float) Math.cos(a0) * (r0 + r1) * 0.5f;
		ay = cy + (float) Math.sin(a0) * (r0 + r1) * 0.5f;
		bx = cx + (float) Math.cos(a1) * (r0 + r1) * 0.5f;
		by = cy + (float) Math.sin(a1) * (r0 + r1) * 0.5f;
		nvgLinearGradient(vg, ax, ay, bx, by, rgba(0, 0, 0, 0, colorA), rgba(0, 0, 0, 128, colorB), paint);
		nvgFillPaint(vg, paint);
		nvgFill(vg);

		nvgRestore(vg);
	}

	public static void renderParagraph(long vg, float x, float y, float width, float fontSize, String font, String text,
			int align, NVGColor color) {
		if (text == null)
			text = "";
		ByteBuffer paragraph = memUTF8(text);

		nvgSave(vg);

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, align);
		nvgTextMetrics(vg, null, null, lineh);

		long start = memAddress(paragraph);
		long end = start + paragraph.remaining();
		int nrows;
		while ((nrows = nnvgTextBreakLines(vg, start, end, width, memAddress(rows), 3)) != 0) {
			for (int i = 0; i < nrows; i++) {
				NVGTextRow row = rows.get(i);
				nvgFillColor(vg, color);
				nnvgText(vg, x, y, row.start(), row.end());
				y += lineh.get(0);
			}
			start = rows.get(nrows - 1).next();
		}

		nvgRestore(vg);
	}

	public static void renderBox(long vg, float x, float y, float w, float h, NVGColor color) {
		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		nvgFillColor(vg, color);
		nvgFill(vg);
	}

	public static void renderSlider(long vg, float pos, float x, float y, float w, float h) {

		nvgSave(vg);
		// Slot
		nvgBeginPath(vg);
		nvgRect(vg, x - 6, y, w + 12, h);
		nvgFillColor(vg, rgba(71, 71, 71, 255, colorA));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 0.5f - 6, y + 0.5f, w - 1 + 12, h - 1);
		nvgStrokeColor(vg, rgba(0, 0, 0, 255, colorA));
		nvgStroke(vg);

		// Knob
		nvgBeginPath(vg);
		nvgRect(vg, x + (int) (pos * w) - 5, y + 1, 10, h - 2);
		nvgFillColor(vg, rgba(200, 200, 200, 255, colorB));
		nvgFill(vg);

		nvgRestore(vg);
	}

	public static void renderContexMenuButton(long vg, String text, String font, float x, float y, float w, float h,
			NVGColor color, float fontSize, boolean highlight, NVGColor highlightColor) {
		nvgSave(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		if (highlight)
			nvgFillColor(vg, highlightColor);
		else
			nvgFillColor(vg, color);
		nvgFill(vg);

		nvgFontSize(vg, fontSize);
		nvgFontFace(vg, font);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		nvgFillColor(vg, rgba(60, 60, 60, 255, colorA));
		nvgText(vg, x + 10f, y + h * 0.5f, text);
		nvgRestore(vg);
	}

	public static void renderToggleButton(long vg, String text, String font, float x, float y, float w, float h,
			NVGColor color, float fontSize, boolean status, NVGColor highlightColor) {
		nvgSave(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x, y, w, h);
		if (status)
			nvgFillColor(vg,
					rgba(highlightColor.r() - 0.4f, highlightColor.g() - 0.4f, highlightColor.b() - 0.4f, 1f, colorA));
		else
			nvgFillColor(vg, rgba(0, 0, 0, 255, colorA));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + 3, y + 3, w - 6, h - 6);
		nvgPathWinding(vg, NVG_HOLE);
		nvgRect(vg, x, y, w, h);
		if (status)
			nvgFillColor(vg, highlightColor);
		else
			nvgFillColor(vg, color);
		nvgFill(vg);

		nvgBeginPath(vg);
		if (status)
			nvgRect(vg, x + w - h + 5, y + 5, h - 10, h - 10);
		else
			nvgRect(vg, x + 5, y + 5, h - 10, h - 10);
		nvgFillColor(vg, color);
		nvgFill(vg);

		nvgRestore(vg);
	}

	public static void renderScrollBarV(long vg, float x, float y, float w, float h, float pos, float sizeV) {
		float scrollv;
		float scrollBarSize = (float) REGISTRY.getRegistryItem("/Voxel/Settings/WindowManager/scrollBarSize");

		nvgSave(vg);
		// Scroll bar
		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y + scrollBarSize, scrollBarSize, h - scrollBarSize * 2f);
		nvgFillColor(vg, rgba(0, 0, 0, 64, colorB));
		nvgFill(vg);

		scrollv = (h / sizeV) * (h / 2);
		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y + scrollBarSize + (h - 8 - scrollv) * pos, scrollBarSize,
				scrollv - scrollBarSize * 2f + 8);
		nvgFillColor(vg, rgba(220, 220, 220, 255, colorB));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y, scrollBarSize, scrollBarSize);
		nvgFillColor(vg, rgba(0, 0, 0, 100, colorB));
		nvgFill(vg);
		nvgBeginPath(vg);
		nvgRect(vg, x + w - scrollBarSize, y + h - scrollBarSize, scrollBarSize, scrollBarSize);
		nvgFillColor(vg, rgba(0, 0, 0, 100, colorB));
		nvgFill(vg);

		nvgBeginPath(vg);
		nvgMoveTo(vg, x + w - scrollBarSize / 2 - 6, y + scrollBarSize / 2 + 5);
		nvgLineTo(vg, x + w - scrollBarSize / 2, y + scrollBarSize / 2 - 5);
		nvgLineTo(vg, x + w - scrollBarSize / 2 + 6, y + scrollBarSize / 2 + 5);
		nvgMoveTo(vg, x + w - scrollBarSize / 2 - 6, y + h - scrollBarSize / 2 - 5);
		nvgLineTo(vg, x + w - scrollBarSize / 2, y + h - scrollBarSize / 2 + 5);
		nvgLineTo(vg, x + w - scrollBarSize / 2 + 6, y + h - scrollBarSize / 2 - 5);
		nvgStrokeColor(vg, rgba(0, 0, 0, 255, colorA));
		nvgStroke(vg);

		nvgRestore(vg);
	}

	public static int generateImageFromTexture(long vg, int texID, int w, int h, int flags) {
		return nvglCreateImageFromHandle(vg, texID, w, h, flags);
	}

	public static ByteBuffer cpToUTF8(int cp) {
		return memUTF8(new String(Character.toChars(cp)), true);
	}

}
