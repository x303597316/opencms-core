/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/gwt/client/util/Attic/CmsDomUtil.java,v $
 * Date   : $Date: 2010/11/18 15:29:49 $
 * Version: $Revision: 1.35 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.gwt.client.util;

import org.opencms.gwt.client.ui.css.I_CmsLayoutBundle;
import org.opencms.gwt.client.util.impl.DOMImpl;
import org.opencms.gwt.client.util.impl.DocumentStyleImpl;
import org.opencms.util.CmsStringUtil;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

/**
 * Utility class to access the HTML DOM.<p>
 * 
 * @author Tobias Herrmann
 * 
 * @version $Revision: 1.35 $
 * 
 * @since 8.0.0
 */
public final class CmsDomUtil {

    /**
     * HTML tag attributes.<p>
     */
    public static enum Attribute {

        /** class. */
        clazz {

            /**
             * @see java.lang.Enum#toString()
             */
            @Override
            public String toString() {

                return "class";
            }
        },

        /** title. */
        title;
    }

    /**
     * Helper class to encapsulate an attribute/value pair.<p>
     */
    public static class AttributeValue {

        /** The attribute. */
        private Attribute m_attr;

        /** The attribute value. */
        private String m_value;

        /**
         * Constructor.<p>
         * 
         * @param attr the attribute
         */
        public AttributeValue(Attribute attr) {

            this(attr, null);
        }

        /**
         * Constructor.<p>
         * 
         * @param attr the attribute
         * @param value the value
         */
        public AttributeValue(Attribute attr, String value) {

            m_attr = attr;
            m_value = value;
        }

        /**
         * Returns the attribute.<p>
         *
         * @return the attribute
         */
        public Attribute getAttr() {

            return m_attr;
        }

        /**
         * Returns the value.<p>
         *
         * @return the value
         */
        public String getValue() {

            return m_value;
        }

        /**
         * Sets the value.<p>
         *
         * @param value the value to set
         */
        public void setValue(String value) {

            m_value = value;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

            StringBuffer sb = new StringBuffer();
            sb.append(m_attr.toString());
            if (m_value != null) {
                sb.append("=\"").append(m_value).append("\"");
            }
            return sb.toString();
        }
    }

    /**
     * CSS Colors.<p>
     */
    public static enum Color {

        /** CSS Color. */
        red;
    }

    /**
     * HTML entities.<p>
     */
    public static enum Entity {

        /** non-breaking space. */
        hellip,

        /** non-breaking space. */
        nbsp;

        /**
         * Returns the HTML code for this entity.<p>
         * 
         * @return the HTML code for this entity
         */
        public String html() {

            return "&" + super.name() + ";";
        }
    }

    /**
     * CSS Properties.<p>
     */
    public static enum Style {

        /** CSS Property. */
        backgroundColor,

        /** CSS Property. */
        backgroundImage,

        /** CSS property. */
        borderLeftWidth,

        /** CSS property. */
        borderRightWidth,

        /** CSS Property. */
        borderStyle,

        /** CSS Property. */
        display,

        /** CSS Property. */
        floatCss {

            /**
             * @see java.lang.Enum#toString()
             */
            @Override
            public String toString() {

                return "float";
            }
        },

        /** CSS Property. */
        fontFamily,

        /** CSS Property. */
        fontSize,

        /** CSS Property. */
        fontSizeAdjust,

        /** CSS Property. */
        fontStretch,

        /** CSS Property. */
        fontStyle,

        /** CSS Property. */
        fontVariant,

        /** CSS Property. */
        fontWeight,

        /** CSS Property. */
        height,

        /** CSS Property. */
        left,

        /** CSS Property. */
        letterSpacing,

        /** CSS Property. */
        lineHeight,

        /** CSS Property. */
        marginBottom,

        /** CSS Property. */
        marginTop,

        /** CSS Property. */
        opacity,

        /** CSS Property. */
        minHeight,

        /** CSS Property. */
        padding,

        /** CSS Property. */
        position,

        /** CSS Property. */
        textAlign,

        /** CSS Property. */
        textDecoration,

        /** CSS Property. */
        textIndent,

        /** CSS Property. */
        textShadow,

        /** CSS Property. */
        textTransform,

        /** CSS Property. */
        top,

        /** CSS Property. */
        visibility,

        /** CSS Property. */
        whiteSpace,

        /** CSS Property. */
        width,

        /** CSS Property. */
        wordSpacing,

        /** CSS Property. */
        wordWrap,

        /** CSS Property. */
        zIndex;

    }

    /**
     * CSS Property values.<p>
     */
    public static enum StyleValue {

        /** CSS Property value. */
        absolute,

        /** CSS Property value. */
        auto,

        /** CSS Property value. */
        hidden,

        /** CSS Property value. */
        none,

        /** CSS Property value. */
        normal,

        /** CSS Property value. */
        nowrap,

        /** CSS Property value. */
        transparent;
    }

    /**
     * HTML Tags.<p>
     */
    public static enum Tag {

        /** HTML Tag. */
        a,

        /** HTML Tag. */
        ALL {

            /**
             * @see java.lang.Enum#toString()
             */
            @Override
            public String toString() {

                return "*";
            }
        },

        /** HTML Tag. */
        b,

        /** HTML Tag. */
        body,

        /** HTML Tag. */
        div,

        /** HTML Tag. */
        h1,

        /** HTML Tag. */
        h2,

        /** HTML Tag. */
        h3,

        /** HTML Tag. */
        h4,

        /** HTML Tag. */
        li,

        /** HTML Tag. */
        p,

        /** HTML Tag. */
        script,

        /** HTML Tag. */
        span,

        /** HTML Tag. */
        ul;
    }

    /** Browser dependent DOM implementation. */
    private static DOMImpl domImpl;

    /** Browser dependent style implementation. */
    private static DocumentStyleImpl styleImpl;

    /**
     * Hidden constructor.<p>
     */
    private CmsDomUtil() {

        // doing nothing
    }

    /**
     * Adds an overlay div to the element.<p>
     * 
     * @param element the element
     */
    public static void addDisablingOverlay(Element element) {

        Element overlay = DOM.createDiv();
        overlay.addClassName(I_CmsLayoutBundle.INSTANCE.generalCss().disablingOverlay());
        element.getStyle().setPosition(Position.RELATIVE);
        element.appendChild(overlay);
    }

    /**
     * Returns if the given client position is over the given element.<p>
     * Use <code>-1</code> for x or y to ignore one ordering orientation.<p>
     * 
     * @param element the element
     * @param x the client x position, use <code>-1</code> to ignore x position 
     * @param y the client y position, use <code>-1</code> to ignore y position
     * 
     * @return <code>true</code> if the given position is over the given element
     */
    public static boolean checkPositionInside(Element element, int x, int y) {

        // ignore x / left-right values for x == -1
        if (x != -1) {
            // check if the mouse pointer is within the width of the target 
            int left = CmsDomUtil.getRelativeX(x, element);
            int offsetWidth = element.getOffsetWidth();
            if ((left <= 0) || (left >= offsetWidth)) {
                return false;
            }
        }
        // ignore y / top-bottom values for y == -1
        if (y != -1) {
            // check if the mouse pointer is within the height of the target 
            int top = CmsDomUtil.getRelativeY(y, element);
            int offsetHeight = element.getOffsetHeight();
            if ((top <= 0) || (top >= offsetHeight)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clones the given element.<p>
     * 
     * It creates a new element with the same tag, and sets the class attribute, 
     * and sets the innerHTML.<p>
     * 
     * @param element the element to clone
     * 
     * @return the cloned element
     */
    public static com.google.gwt.user.client.Element clone(Element element) {

        com.google.gwt.user.client.Element elementClone = DOM.createElement(element.getTagName());
        elementClone.setClassName(element.getClassName());
        elementClone.setInnerHTML(element.getInnerHTML());
        return elementClone;
    }

    /**
     * Generates a closing tag.<p>
     * 
     * @param tag the tag to use
     * 
     * @return HTML code
     */
    public static String close(Tag tag) {

        return "</" + tag.name() + ">";
    }

    /**
     * This method will create an {@link com.google.gwt.user.client.Element} for the given HTML. 
     * The HTML should have a single root tag, if not, the first tag will be used and all others discarded.
     * Script-tags will be ignored.
     * 
     * @param html the HTML to use for the element
     * 
     * @return the created element
     * 
     * @throws Exception if something goes wrong 
     */
    public static com.google.gwt.user.client.Element createElement(String html) throws Exception {

        com.google.gwt.user.client.Element wrapperDiv = DOM.createDiv();
        wrapperDiv.setInnerHTML(html);
        com.google.gwt.user.client.Element elementRoot = (com.google.gwt.user.client.Element)wrapperDiv.getFirstChildElement();
        DOM.removeChild(wrapperDiv, elementRoot);
        // just in case we have a script tag outside the root HTML-tag
        while ((elementRoot != null) && (elementRoot.getTagName().toLowerCase().equals(Tag.script.name()))) {
            elementRoot = (com.google.gwt.user.client.Element)wrapperDiv.getFirstChildElement();
            DOM.removeChild(wrapperDiv, elementRoot);
        }
        if (elementRoot == null) {
            CmsDebugLog.getInstance().printLine(
                "Could not create element as the given HTML has no appropriate root element");
            throw new UnsupportedOperationException(
                "Could not create element as the given HTML has no appropriate root element");
        }
        return elementRoot;

    }

    /**
     * Convenience method to assemble the HTML to use for a button face.<p>
     * 
     * @param text text the up face text to set, set to <code>null</code> to not show any
     * @param imageClass the up face image class to use, set to <code>null</code> to not show any
     * @param align the alignment of the text in reference to the image
     * 
     * @return the HTML
     */
    public static String createFaceHtml(String text, String imageClass, HorizontalAlignmentConstant align) {

        StringBuffer sb = new StringBuffer();
        if (align == HasHorizontalAlignment.ALIGN_LEFT) {
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(text)) {
                sb.append(text.trim());
            }
        }
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(imageClass)) {
            String clazz = imageClass;
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(text)) {
                if (align == HasHorizontalAlignment.ALIGN_LEFT) {
                    clazz += " " + I_CmsLayoutBundle.INSTANCE.buttonCss().spacerLeft();
                } else {
                    clazz += " " + I_CmsLayoutBundle.INSTANCE.buttonCss().spacerRight();
                }
            }
            AttributeValue attr = new AttributeValue(Attribute.clazz, clazz);
            sb.append(enclose(Tag.span, "", attr));
        }
        if (align == HasHorizontalAlignment.ALIGN_RIGHT) {
            if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(text)) {
                sb.append(text.trim());
            }
        }
        return sb.toString();
    }

    /**
     * Creates an iFrame element with the given name attribute.<p>
     * 
     * @param name the name attribute value
     * 
     * @return the iFrame element
     */
    public static com.google.gwt.dom.client.Element createIFrameElement(String name) {

        return getDOMImpl().createIFrameElement(Document.get(), name);
    }

    /**
     * Encloses the given text with the given tag.<p>
     * 
     * @param tag the tag to use
     * @param text the text to enclose
     * @param attrs the optional tag attributes
     * 
     * @return HTML code
     */
    public static String enclose(Tag tag, String text, AttributeValue... attrs) {

        return open(tag, attrs) + text + close(tag);
    }

    /**
     * Triggers a mouse-out event for the given element.<p>
     * 
     * Useful in case something is capturing all events.<p>
     * 
     * @param element the element to use
     */
    public static void ensureMouseOut(Element element) {

        NativeEvent nativeEvent = Document.get().createMouseOutEvent(0, 0, 0, 0, 0, false, false, false, false, 0, null);
        element.dispatchEvent(nativeEvent);
    }

    /**
     * Ensures that the given element is visible.<p>
     * 
     * Assuming the scrollbars are on the container element, and that the element is a child of the container element.<p>
     * 
     * @param containerElement the container element, has to be parent of the element
     * @param element the element to be seen
     * @param animationTime the animation time for scrolling, use zero for no animation
     */
    public static void ensureVisible(final Element containerElement, Element element, int animationTime) {

        Element item = element;
        int realOffset = 0;
        while ((item != null) && (item != containerElement)) {
            realOffset += element.getOffsetTop();
            item = item.getOffsetParent();
        }
        final int endScrollTop = realOffset - containerElement.getOffsetHeight() / 2;

        if (animationTime <= 0) {
            // no animation
            containerElement.setScrollTop(endScrollTop);
            return;
        }
        final int startScrollTop = containerElement.getScrollTop();
        (new Animation() {

            /**
             * @see com.google.gwt.animation.client.Animation#onUpdate(double)
             */
            @Override
            protected void onUpdate(double progress) {

                containerElement.setScrollTop(startScrollTop + (int)((endScrollTop - startScrollTop) * progress));
            }
        }).run(animationTime);
    }

    /**
     * Returns the given element or it's closest ancestor with the given class.<p>
     * 
     * Returns <code>null</code> if no appropriate element was found.<p>
     * 
     * @param element the element
     * @param className the class name
     * 
     * @return the matching element
     */
    public static Element getAncestor(Element element, String className) {

        if (hasClass(className, element)) {
            return element;
        }
        if (element.getTagName().equalsIgnoreCase(Tag.body.name())) {
            return null;
        }
        return getAncestor(element.getParentElement(), className);
    }

    /**
     * Returns the given element or it's closest ancestor with the given tag name.<p>
     * 
     * Returns <code>null</code> if no appropriate element was found.<p>
     * 
     * @param element the element
     * @param tag the tag name
     * 
     * @return the matching element
     */
    public static Element getAncestor(Element element, Tag tag) {

        if ((element == null) || (tag == null)) {
            return null;
        }
        if (element.getTagName().equalsIgnoreCase(tag.name())) {
            return element;
        }
        if (element.getTagName().equalsIgnoreCase(Tag.body.name())) {
            return null;
        }
        return getAncestor(element.getParentElement(), tag);
    }

    /**
     * Returns the given element or it's closest ancestor with the given tag and class.<p>
     * 
     * Returns <code>null</code> if no appropriate element was found.<p>
     * 
     * @param element the element
     * @param tag the tag name
     * @param className the class name
     * 
     * @return the matching element
     */
    public static Element getAncestor(Element element, Tag tag, String className) {

        if (element.getTagName().equalsIgnoreCase(tag.name()) && hasClass(className, element)) {
            return element;
        }
        if (element.getTagName().equalsIgnoreCase(Tag.body.name())) {
            return null;
        }
        return getAncestor(element.getParentElement(), tag, className);
    }

    /**
     * Returns the computed style of the given element.<p>
     * 
     * @param element the element
     * @param style the CSS property 
     * 
     * @return the currently computed style
     */
    public static String getCurrentStyle(Element element, Style style) {

        if (styleImpl == null) {
            styleImpl = GWT.create(DocumentStyleImpl.class);
        }
        return styleImpl.getCurrentStyle(element, style.toString());
    }

    /**
     * Returns the computed style of the given element as floating point number.<p>
     * 
     * @param element the element
     * @param style the CSS property 
     * 
     * @return the currently computed style
     */
    public static double getCurrentStyleFloat(Element element, Style style) {

        String currentStyle = getCurrentStyle(element, style);
        return CmsClientStringUtil.parseFloat(currentStyle);
    }

    /**
     * Returns the computed style of the given element as number.<p>
     * 
     * @param element the element
     * @param style the CSS property 
     * 
     * @return the currently computed style
     */
    public static int getCurrentStyleInt(Element element, Style style) {

        String currentStyle = getCurrentStyle(element, style);
        return CmsClientStringUtil.parseInt(currentStyle);
    }

    /**
     * Returns all elements from the DOM with the given CSS class.<p>
     * 
     * @param className the class name to look for
     * 
     * @return the matching elements
     */
    public static List<Element> getElementsByClass(String className) {

        return getElementsByClass(className, Tag.ALL, Document.get().getBody());
    }

    /**
     * Returns all elements with the given CSS class including the root element.<p>
     * 
     * @param className the class name to look for
     * @param rootElement the root element of the search
     * 
     * @return the matching elements
     */
    public static List<Element> getElementsByClass(String className, Element rootElement) {

        return getElementsByClass(className, Tag.ALL, rootElement);

    }

    /**
     * Returns all elements from the DOM with the given CSS class and tag name.<p>
     * 
     * @param className the class name to look for
     * @param tag the tag
     * 
     * @return the matching elements
     */
    public static List<Element> getElementsByClass(String className, Tag tag) {

        return getElementsByClass(className, tag, Document.get().getBody());
    }

    /**
     * Returns all elements with the given CSS class and tag name including the root element.<p>
     * 
     * @param className the class name to look for
     * @param tag the tag
     * @param rootElement the root element of the search
     * 
     * @return the matching elements
     */
    public static List<Element> getElementsByClass(String className, Tag tag, Element rootElement) {

        if ((rootElement == null) || (className == null) || (className.trim().length() == 0) || (tag == null)) {
            return null;
        }
        className = className.trim();
        List<Element> result = new ArrayList<Element>();
        if (internalHasClass(className, rootElement)) {
            result.add(rootElement);
        }
        NodeList<Element> elements = rootElement.getElementsByTagName(tag.toString());
        for (int i = 0; i < elements.getLength(); i++) {
            if (internalHasClass(className, elements.getItem(i))) {
                result.add(elements.getItem(i));
            }
        }
        return result;
    }

    /**
     * Returns the element position relative to its siblings.<p>
     * 
     * @param e the element to get the position for
     * 
     * @return the position, or <code>-1</code> if not found
     */
    public static int getPosition(Element e) {

        NodeList<Node> childNodes = e.getParentElement().getChildNodes();
        for (int i = childNodes.getLength(); i >= 0; i--) {
            if (childNodes.getItem(i) == e) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the next ancestor to the element with an absolute, fixed or relative position.<p>
     * 
     * @param child the element
     * 
     * @return the positioning parent element (may be <code>null</code>)
     */
    public static Element getPositioningParent(Element child) {

        Element parent = child.getParentElement();
        while (parent != null) {
            String parentPositioning = CmsDomUtil.getCurrentStyle(parent, Style.position);
            if (Position.RELATIVE.getCssName().equals(parentPositioning)
                || Position.ABSOLUTE.getCssName().equals(parentPositioning)
                || Position.FIXED.getCssName().equals(parentPositioning)) {
                return parent;
            }
            parent = parent.getParentElement();
        }
        return null;
    }

    /**
     * Gets the horizontal position of the given x-coordinate relative to a given element.<p>
     * 
     * @param x the coordinate to use 
     * @param target the element whose coordinate system is to be used
     * 
     * @return the relative horizontal position
     * 
     * @see com.google.gwt.event.dom.client.MouseEvent#getRelativeX(com.google.gwt.dom.client.Element)
     */
    public static int getRelativeX(int x, Element target) {

        return x - target.getAbsoluteLeft() + target.getScrollLeft() + target.getOwnerDocument().getScrollLeft();
    }

    /**
     * Gets the vertical position of the given y-coordinate relative to a given element.<p>
     * 
     * @param y the coordinate to use 
     * @param target the element whose coordinate system is to be used
     * 
     * @return the relative vertical position
     * 
     * @see com.google.gwt.event.dom.client.MouseEvent#getRelativeY(com.google.gwt.dom.client.Element)
     */
    public static int getRelativeY(int y, Element target) {

        return y - target.getAbsoluteTop() + target.getScrollTop() + target.getOwnerDocument().getScrollTop();
    }

    /**
     * Returns the Z index from the given style.<p>
     * 
     * This is a workaround for a bug with {@link com.google.gwt.dom.client.Style#getZIndex()} which occurs with IE in 
     * hosted mode.<p>
     * 
     * @param style the style object from which the Z index property should be fetched 
     * 
     * @return the z index
     */
    public static native String getZIndex(com.google.gwt.dom.client.Style style)
    /*-{
        return "" + style.zIndex;
    }-*/;

    /**
     * Utility method to determine if the given element has a set background.<p>
     * 
     * @param element the element
     * 
     * @return <code>true</code> if the element has a background set
     */
    public static boolean hasBackground(Element element) {

        String backgroundColor = CmsDomUtil.getCurrentStyle(element, Style.backgroundColor);
        String backgroundImage = CmsDomUtil.getCurrentStyle(element, Style.backgroundImage);
        if ((backgroundColor.equals(StyleValue.transparent.toString()))
            && ((backgroundImage == null) || (backgroundImage.trim().length() == 0) || backgroundImage.equals(StyleValue.none.toString()))) {
            return false;
        }
        return true;
    }

    /**
     * Utility method to determine if the given element has a set border.<p>
     * 
     * @param element the element
     * 
     * @return <code>true</code> if the element has a border
     */
    public static boolean hasBorder(Element element) {

        String borderStyle = CmsDomUtil.getCurrentStyle(element, Style.borderStyle);
        if ((borderStyle == null) || borderStyle.equals(StyleValue.none.toString()) || (borderStyle.length() == 0)) {
            return false;
        }
        return true;

    }

    /**
     * Indicates if the given element has a CSS class.<p>
     * 
     * @param className the class name to look for
     * @param element the element
     * 
     * @return <code>true</code> if the element has the given CSS class
     */
    public static boolean hasClass(String className, Element element) {

        return internalHasClass(className.trim(), element);
    }

    /**
     * Generates an opening tag.<p>
     * 
     * @param tag the tag to use
     * @param attrs the optional tag attributes
     * 
     * @return HTML code
     */
    public static String open(Tag tag, AttributeValue... attrs) {

        StringBuffer sb = new StringBuffer();
        sb.append("<").append(tag.name());
        for (AttributeValue attr : attrs) {
            sb.append(" ").append(attr.toString());
        }
        sb.append(">");
        return sb.toString();
    }

    /**
     * Positions an element in the DOM relative to another element.<p>
     * 
     * @param elem the element to position
     * @param referenceElement the element relative to which the first element should be positioned
     * @param dx the x offset relative to the reference element
     * @param dy the y offset relative to the reference element 
     */
    public static void positionElement(Element elem, Element referenceElement, int dx, int dy) {

        com.google.gwt.dom.client.Style style = elem.getStyle();
        style.setLeft(0, Unit.PX);
        style.setTop(0, Unit.PX);
        int myX = elem.getAbsoluteLeft();
        int myY = elem.getAbsoluteTop();
        int refX = referenceElement.getAbsoluteLeft();
        int refY = referenceElement.getAbsoluteTop();
        int newX = refX - myX + dx;
        int newY = refY - myY + dy;
        style.setLeft(newX, Unit.PX);
        style.setTop(newY, Unit.PX);
    }

    /**
     * Positions an element inside the given parent, reordering the content of the parent and returns the new position index.<p>
     * This is none absolute positioning. Use for drag and drop reordering of drop targets.<p>
     * Use <code>-1</code> for x or y to ignore one ordering orientation.<p>
     * 
     * @param element the child element
     * @param parent the parent element
     * @param currentIndex the current index position of the element, use -1 if element is not attached to the parent yet 
     * @param x the client x position, use <code>-1</code> to ignore x position 
     * @param y the client y position, use <code>-1</code> to ignore y position
     * 
     * @return the new index position
     */
    public static int positionElementInside(Element element, Element parent, int currentIndex, int x, int y) {

        if ((x == -1) && (y == -1)) {
            // this is wrong usage, do nothing
            CmsDebugLog.getInstance().printLine("this is wrong usage, doing nothing");
            return currentIndex;
        }
        int indexCorrection = 0;
        for (int index = 0; index < parent.getChildCount(); index++) {
            Node node = parent.getChild(index);
            if (!(node instanceof Element)) {
                continue;
            }
            Element child = (Element)node;
            if (child == element) {
                indexCorrection = 1;
            }
            String positioning = child.getStyle().getPosition();
            if (positioning.equals(Position.ABSOLUTE.getCssName()) || positioning.equals(Position.FIXED.getCssName())) {
                // only not 'position:absolute' elements into account, 
                // not visible children will be excluded in the next condition
                continue;
            }
            int left = 0;
            int width = 0;
            int top = 0;
            int height = 0;
            if (x != -1) {
                // check if the mouse pointer is within the width of the element 
                left = CmsDomUtil.getRelativeX(x, child);
                width = child.getOffsetWidth();
                if ((left <= 0) || (left >= width)) {
                    continue;
                }
            }
            if (y != -1) {
                // check if the mouse pointer is within the height of the element 
                top = CmsDomUtil.getRelativeY(y, child);
                height = child.getOffsetHeight();
                if ((top <= 0) || (top >= height)) {
                    continue;
                }
            } else {
                if (child == element) {
                    return currentIndex;
                }

                if (left < width / 2) {
                    parent.insertBefore(element, child);
                    currentIndex = index - indexCorrection;
                    return currentIndex;
                } else {
                    parent.insertAfter(element, child);
                    currentIndex = index + 1 - indexCorrection;
                    return currentIndex;
                }
            }
            if (child == element) {
                return currentIndex;
            }

            if (top < height / 2) {
                parent.insertBefore(element, child);
                currentIndex = index - indexCorrection;
                return currentIndex;
            } else {
                parent.insertAfter(element, child);
                currentIndex = index + 1 - indexCorrection;
                return currentIndex;
            }

        }
        // not over any child position
        if ((currentIndex >= 0) && (element.getParentElement() == parent)) {
            // element is already attached to this parent and no new position available
            // don't do anything
            return currentIndex;
        }
        int top = CmsDomUtil.getRelativeY(y, parent);
        int offsetHeight = parent.getOffsetHeight();
        if ((top >= offsetHeight / 2)) {
            // over top half, insert as first child
            parent.insertFirst(element);
            currentIndex = 0;
            return currentIndex;
        }
        // over bottom half, insert as last child
        parent.appendChild(element);
        currentIndex = parent.getChildCount() - 1;
        return currentIndex;
    }

    /**
     * Removes any present overlay from the element and it's children.<p>
     * 
     * @param element the element
     */
    public static void removeDisablingOverlay(Element element) {

        List<Element> overlays = CmsDomUtil.getElementsByClass(
            I_CmsLayoutBundle.INSTANCE.generalCss().disablingOverlay(),
            Tag.div,
            element);
        if (overlays == null) {
            return;
        }
        for (Element overlay : overlays) {
            overlay.getParentElement().getStyle().clearPosition();
            overlay.removeFromParent();
        }
        element.removeClassName(I_CmsLayoutBundle.INSTANCE.generalCss().hideOverlay());
    }

    /**
     * Sets a CSS class to show or hide a given overlay. Will not add an overlay to the element.<p>
     * 
     * @param element the parent element of the overlay
     * @param show <code>true</code> to show the overlay
     */
    public static void showOverlay(Element element, boolean show) {

        if (show) {
            element.removeClassName(I_CmsLayoutBundle.INSTANCE.generalCss().hideOverlay());
        } else {
            element.addClassName(I_CmsLayoutBundle.INSTANCE.generalCss().hideOverlay());
        }
    }

    /**
     * Returns the DOM implementation.<p>
     * 
     * @return the DOM implementation
     */
    private static DOMImpl getDOMImpl() {

        if (domImpl == null) {
            domImpl = GWT.create(DOMImpl.class);
        }
        return domImpl;
    }

    /**
     * Internal method to indicate if the given element has a CSS class.<p>
     * 
     * @param className the class name to look for
     * @param element the element
     * 
     * @return <code>true</code> if the element has the given CSS class
     */
    private static boolean internalHasClass(String className, Element element) {

        String elementClass = element.getClassName().trim();
        boolean hasClass = elementClass.equals(className);
        hasClass |= elementClass.contains(" " + className + " ");
        hasClass |= elementClass.startsWith(className + " ");
        hasClass |= elementClass.endsWith(" " + className);

        return hasClass;
    }
}
