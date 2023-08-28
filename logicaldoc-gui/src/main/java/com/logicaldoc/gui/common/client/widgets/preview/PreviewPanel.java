package com.logicaldoc.gui.common.client.widgets.preview;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.GuiLog;
import com.logicaldoc.gui.common.client.util.DocumentProtectionManager;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.MessageLabel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel is used to show the document preview.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.1.1
 */
public class PreviewPanel extends VLayout {

	protected HTMLFlow preview = null;

	protected HTMLFlow media = null;

	protected HTMLFlow html = null;

	protected HTMLFlow dicom = null;

	protected MailPreviewPanel mail = null;

	protected Canvas reload = null;

	protected Label disabled = null;

	protected long docId;

	protected boolean accessGranted = false;

	protected GUIDocument document;

	protected int width;

	protected int height;

	protected boolean redrawing = false;

	public PreviewPanel(final GUIDocument document) {
		this.document = document;
		this.docId = document.getId();
	}

	@Override
	protected void onDraw() {
		if (!isPreviewAvailable()) {
			showDisabledPanel();
			return;
		}

		DocumentProtectionManager.askForPassword(docId, doc -> onAccessGranted(document));

		addResizedHandler(event -> doResize());
	}

	protected void onAccessGranted(final GUIDocument document) {
		accessGranted = true;

		if (Util.isMediaFile(document.getFileName().toLowerCase())) {
			reloadMedia();
		} else if (Util.isWebContentFile(document.getFileName().toLowerCase())) {
			reloadHTML();
		} else if (Util.isEmailFile(document.getFileName().toLowerCase())) {
			reloadMail();
		} else if (Util.isDICOMFile(document.getFileName().toLowerCase())) {
			FolderService.Instance.get().getFolder(document.getFolder().getId(), false, false, false,
					new AsyncCallback<GUIFolder>() {

						@Override
						public void onFailure(Throwable caught) {
							GuiLog.serverError(caught);
						}

						@Override
						public void onSuccess(GUIFolder folder) {
							if (folder.isDownload())
								reloadDICOM();
							else
								reloadPreview();
						}
					});
		} else {
			reloadPreview();
		}

		redraw();
	}

	protected boolean isPreviewAvailable() {
		return Menu.enabled(Menu.PREVIEW);
	}

	protected void doResize() {
		if (getWidth() < 10L) {
			// The panel has been closed
			clearContent();
			width = 0;
			height = 0;
			html = null;
			preview = null;
			dicom = null;
			media = null;
			mail = null;
			reload = null;
		} else if (!redrawing && (width != getWidth() || height != getHeight())) {
			width = getWidth();
			height = getHeight();
			clearContent();
			showReloadPanel();
		}
	}

	@Override
	public synchronized void redraw() {
		redrawing = true;
		try {
			clearContent();
			width = getWidth();
			height = getHeight();
			if (!isPreviewAvailable()) {
				showDisabledPanel();
				return;
			}

			if (accessGranted && getWidth() > 10) {
				if (preview != null) {
					reloadPreview();
				} else if (html != null) {
					reloadHTML();
				} else if (dicom != null) {
					reloadDICOM();
				} else if (media != null) {
					reloadMedia();
				} else if (mail != null) {
					reloadMail();
				}
			}
		} finally {
			redrawing = false;
		}
	}

	/**
	 * Reloads a mail preview
	 */
	protected void reloadMail() {
		DocumentService.Instance.get().extractEmail(docId, document.getFileVersion(), new AsyncCallback<GUIEmail>() {
			@Override
			public void onFailure(Throwable caught) {
				GuiLog.serverError(caught);
			}

			@Override
			public void onSuccess(final GUIEmail email) {
				if (mail != null)
					removeMember(mail);
				mail = new MailPreviewPanel(email, document, getWidth());
				addMember(mail);
			}
		});
	}

	/**
	 * Reloads a media preview
	 */
	protected void reloadMedia() {
		if (media != null)
			removeMember(media);

		media = new HTMLFlow();
		String contents = "";

		try {
			String url = Util.downloadURL(docId, document.getFileVersion(), false);

			if (Util.isAudioFile(document.getFileName()))
				contents = Util.audioHTML(url);
			else
				contents = Util.videoHTML(url, getWidth() != null ? "" + (getWidth() - 2) : "",
						getHeight() != null ? "" + (getHeight() - 1) : "");
		} catch (Exception t) {
			GuiLog.info(t.getMessage(), null);
		}

		media.setContents(contents);
		addMember(media);
	}

	/**
	 * Reloads a preview for HTML documents.
	 */
	protected void reloadHTML() {
		if (html != null)
			removeMember(html);

		html = new HTMLPane();
		html.setShowEdges(false);
		html.setContentsURL(Util.downloadURL(docId, document.getFileVersion(), "safe.html", false));
		html.setContentsType(ContentsType.FRAGMENT);

		setWidth100();
		addMember(html);
	}

	/**
	 * Reloads a preview for DICOM documents.
	 */
	protected void reloadDICOM() {
		if (dicom != null)
			removeMember(dicom);

		dicom = new HTMLFlow();
		String url = dicomUrl();
		dicom.setContents(
				"<iframe src='" + url + "' style='border:0px solid white; width:" + (getWidth() - 1) + "px; height:"
						+ (getHeight() - 1) + "px; overflow:hidden;' scrolling='no' seamless='seamless'></iframe>");
		addMember(dicom);
	}

	/**
	 * Reloads a preview.
	 */
	protected void reloadPreview() {
		if (preview != null)
			removeMember(preview);

		preview = new HTMLFlow();
		String contents = "";

		long maxFileSize = Session.get().getConfigAsLong("gui.preview.maxfilesize") * 1024 * 1024;
		if (maxFileSize > 0 && maxFileSize < document.getFileSize()) {
			contents = "<div><br><center><b>" + I18N.message("doctoobigtoberendered") + "</b></center></br></div>";
		} else {
			try {
				contents = "<iframe src='" + generalPreviewUrl() + "' style='border:0px solid white; width:"
						+ (getWidth() - 1) + "px; height:" + (getHeight() - 1)
						+ "px; overflow:hidden;'  scrolling='no' seamless='seamless'></iframe>";
			} catch (Exception t) {
				// Nothing to do
			}
		}
		preview.setContents(contents);
		addMember(preview);
	}

	protected String generalPreviewUrl() {
		String locale = Session.get().getUser().getLanguage();
		return Util.contextPath() + "prev/index.jsp?docId=" + docId
				+ (document.getFileVersion() != null ? "&fileVersion=" + document.getFileVersion() : "")
				+ "&control=preview&locale=" + locale + "#locale=" + locale.replace('_', '-');
	}

	protected String dicomUrl() {
		return Util.contextPath() + "dicom/index.jsp?input=" + URL.encodeQueryString(
				Util.downloadURL(docId, document.getFileVersion()) + "&sid=" + Session.get().getSid());
	}
	
	protected void clearContent() {
		if (reload != null) {
			removeMember(reload);
		}
		if (preview != null) {
			removeMember(preview);
		}
		if (html != null) {
			removeMember(html);
		}
		if (dicom != null) {
			removeMember(dicom);
		}
		if (media != null) {
			removeMember(media);
		}
		if (mail != null) {
			removeMember(mail);
		}
		if (disabled != null) {
			removeMember(disabled);
		}
	}

	private void showReloadPanel() {
		IButton reloadButton = new IButton(I18N.message("reload"));
		reloadButton.setAutoFit(true);
		reloadButton.setSnapTo("C");
		reloadButton.addClickHandler(event -> redraw());

		reload = new Canvas();
		reload.setWidth100();
		reload.setHeight100();
		reload.setAlign(Alignment.CENTER);
		reload.addChild(reloadButton);

		addMember(reload);
	}

	/**
	 * Displays the label when the preview menu is not enabled
	 */
	private void showDisabledPanel() {
		disabled = new MessageLabel(
				new GUIMessage("<b>" + I18N.message("previewdisabled") + "</b>", GUIMessage.PRIO_WARN), false);
		disabled.setHeight(30);
		disabled.setPadding(10);
		disabled.setAlign(Alignment.CENTER);
		disabled.setValign(VerticalAlignment.CENTER);
		disabled.setShowEdges(false);
		addMember(disabled);
	}
}
