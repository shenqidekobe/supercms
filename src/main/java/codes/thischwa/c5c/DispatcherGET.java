/*
 * C5Connector.Java - The Java backend for the filemanager of corefive.
 * It's a bridge between the filemanager and a storage backend and 
 * works like a transparent VFS or proxy.
 * Copyright (C) Thilo Schwarz
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package codes.thischwa.c5c;

import java.awt.Dimension;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.log4j.Log4j2;
import codes.thischwa.c5c.GenericConnector.StreamContent;
import codes.thischwa.c5c.exception.C5CException;
import codes.thischwa.c5c.exception.FilemanagerException.Key;
import codes.thischwa.c5c.filemanager.FilemanagerConfig;
import codes.thischwa.c5c.requestcycle.Context;
import codes.thischwa.c5c.requestcycle.RequestData;
import codes.thischwa.c5c.requestcycle.response.GenericResponse;
import codes.thischwa.c5c.requestcycle.response.mode.CreateFolder;
import codes.thischwa.c5c.requestcycle.response.mode.Delete;
import codes.thischwa.c5c.requestcycle.response.mode.Download;
import codes.thischwa.c5c.requestcycle.response.mode.EditFile;
import codes.thischwa.c5c.requestcycle.response.mode.FileInfo;
import codes.thischwa.c5c.requestcycle.response.mode.FolderInfo;
import codes.thischwa.c5c.requestcycle.response.mode.Prieview;
import codes.thischwa.c5c.requestcycle.response.mode.Rename;
import codes.thischwa.c5c.requestcycle.response.mode.ShowThumbnail;
import codes.thischwa.c5c.util.FileUtils;
import codes.thischwa.c5c.util.VirtualFile;

/**
 * Dispatches the GET-request from the 'main' servlet {@link ConnectorServlet} to the implementation of the object which extends the
 * {@link Connector}. The parameters of the request will be prepared before dispatching them to connector.
 */
@Log4j2
final class DispatcherGET extends GenericDispatcher {

	/**
	 * Instantiates and initializes the connector (object which extends the {@link GenericConnector});
	 * 
	 * @param connector
	 *            the implementation of the {@link Connector} interface
	 */
	DispatcherGET(Connector connector) {
		super(connector);
	}

	/**
	 * Processes the get-request. Known modes are: getinfo, getfolder, rename, delete, download.
	 * 
	 * @return the response
	 */
	@Override
	GenericResponse doRequest() {
		log.debug("Entering DispatcherGET#doRequest");
		Set<String> imageExtensions = UserObjectProxy.getFilemanagerConfig().getImages().getExtensions();
		connector.setImageExtensions(imageExtensions);
		try {
			Context ctx = RequestData.getContext();
			FilemanagerAction mode = ctx.getMode();
			HttpServletRequest req = RequestData.getContext().getServletRequest();
			GenericResponse resp = null;
			switch(mode) {
			case FOLDER: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				if(connector.isProtected(backendPath)) {
					FileInfo fi = new FileInfo(urlPath, true, true);
					fi.setFileProperties(new GenericConnector.FileProperties(urlPath, true, null));
					fi.setError(UserObjectProxy.getFilemanagerErrorMessage(Key.NotAllowedSystem), -1);
					return fi;
				}
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				log.debug("* getFolder -> urlPath: {}, backendPath: {}, needSize: {}", urlPath, backendPath, needSize);
				Set<GenericConnector.FileProperties> props = connector.getFolder(backendPath, needSize);
				resp = buildFolder(urlPath, props);
				break;
			}
			case INFO: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				boolean needSize = Boolean.parseBoolean(req.getParameter("getsize"));
				log.debug("* getInfo -> urlPath: {}, backendPath {}, needSize: {}", urlPath, backendPath, needSize);
				GenericConnector.FileProperties fp = connector.getInfo(backendPath, needSize);
				resp = buildFileInfo(urlPath, fp);
				break;
			}
			case RENAME: {
				String oldUrlPath = req.getParameter("old");
				String oldBackendPath = buildBackendPath(oldUrlPath);
				String newName = req.getParameter("new");
				String sanitizedName = FileUtils.sanitizeName(newName);
				log.debug("* rename -> oldUrlPath: {}, backendPath: {}, new name: {}, santized new name: {}", oldUrlPath,
						oldBackendPath, newName, sanitizedName);
				boolean isDirectory = connector.rename(oldBackendPath, sanitizedName);
				resp = buildRename(oldUrlPath, sanitizedName, isDirectory);
				break;
			}
			case CREATEFOLDER: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				String folderName = req.getParameter("name");
				String sanitizedFolderName = FileUtils.sanitizeName(folderName);
				log.debug("* createFolder -> urlPath: {}, backendPath: {}, name: {}, sanitized name: {}", urlPath, backendPath,
						folderName, sanitizedFolderName);
				connector.createFolder(backendPath, sanitizedFolderName);
				resp = buildCreateFolder(urlPath, sanitizedFolderName);
				break;
			}
			case DELETE: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				log.debug("* delete -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				boolean isDirectory = connector.delete(backendPath);
				resp = buildDelete(urlPath, isDirectory);
				break;
			}
			case DOWNLOAD: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				log.debug("* download -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				StreamContent sc = connector.download(backendPath);
				resp = buildDownload(backendPath, sc);
				break;
			}
			case THUMBNAIL: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				log.debug("* thumbnail -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				resp = buildThumbnailView(backendPath);
				break;
			}
			case PREVIEW: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				boolean thumbnail = Boolean.valueOf(req.getParameter("thumbnail"));
				log.debug("* thumbnail -> urlPath: {}, backendPath: {}, thumbnail: {}", urlPath, backendPath, thumbnail);
				//resp = buildThumbnailView(backendPath);		
				if(thumbnail) {
					resp = buildThumbnailView(backendPath);					
				} else {
					//StreamContent sc = connector.preview(backendPath, UserObjectProxy.getPreviewDimension());
					StreamContent sc = connector.buildThumbnail(backendPath, UserObjectProxy.getPreviewDimension());
					resp = buildPrieview(backendPath, sc);
				}
				break;
			}
			case EDITFILE: {
				String urlPath = req.getParameter("path");
				String backendPath = buildBackendPath(urlPath);
				log.debug("* editfile -> urlPath: {}, backendPath: {}", urlPath, backendPath);
				resp = new EditFile(backendPath, connector.editFile(backendPath));
				break;
			}
			default: {
				log.error("Unknown 'mode' for GET: {}", req.getParameter("mode"));
				throw new C5CException(UserObjectProxy.getFilemanagerErrorMessage(Key.ModeError));
			}
			}
			return resp;
		} catch (C5CException e) {
			return ErrorResponseFactory.buildException(e);
		}
	}

	private FolderInfo buildFolder(String urlPath, Set<GenericConnector.FileProperties> fileProperties) {
		FolderInfo folderInfo = buildFolderInfo();
		if(fileProperties == null)
			return folderInfo;
		List<GenericConnector.FileProperties> props = new ArrayList<>(fileProperties);
		sortFileProperties(props, UserObjectProxy.getFilemanagerConfig().getOptions().getFileSorting());
		List<FileInfo> infos = new ArrayList<>(props.size());
		for(GenericConnector.FileProperties fp : props) {
			FileInfo fileInfo = buildFileInfo(urlPath, fp);
			infos.add(fileInfo);
			add(folderInfo, fileInfo);
		}
		return folderInfo;
	}

	private FileInfo buildFileInfo(String urlPath, GenericConnector.FileProperties fp) {
		FilemanagerConfig fConfig = UserObjectProxy.getFilemanagerConfig();
		FileInfo fi = new FileInfo(urlPath, fp.isDir(), fp.isProtected());
		fi.setFileProperties(fp);
		setCapabilities(fi, urlPath);
		VirtualFile vf = new VirtualFile(fp);
		if(fConfig.getOptions().isShowThumbs() && vf.getType()==VirtualFile.Type.file && fConfig.getImages().getExtensions().contains(vf.getExtension())) {
			// attention: urlPath can be with or without a file name!
			HttpServletRequest req = RequestData.getContext().getServletRequest();
			String previewUrlPath = (urlPath.endsWith(vf.getName())) ? urlPath : urlPath.concat(fp.getName());
			String query =  String.format("?mode=%s&path=%s&t=%s", FilemanagerAction.PREVIEW.getParameterName(), encode(previewUrlPath), Calendar.getInstance().getTimeInMillis());
			String preview = String.format("%s%s%s",req.getContextPath(), req.getServletPath(), query); 
			fi.setPreviewPath(preview);
		} else {
			fi.setPreviewPath(UserObjectProxy.getDefaultIconPath(vf));
		}
		return fi;
	}
	
	private void add(FolderInfo folderInfo, FileInfo fileInfo) {
		folderInfo.add(fileInfo);
	}

	private Rename buildRename(String urlPath, String newSanitizedName, boolean isDirectory) {
		return new Rename(urlPath, newSanitizedName, isDirectory);
	}

	private Delete buildDelete(String path, boolean isDirectory) {
		String delPath = path;
		if(isDirectory && !delPath.endsWith(Constants.defaultSeparator))
			delPath += Constants.defaultSeparator;
		return new Delete(delPath);
	}

	private FolderInfo buildFolderInfo() {
		return new FolderInfo();
	}

	private CreateFolder buildCreateFolder(String parentUrlPath, String folderName) {
		return new CreateFolder(parentUrlPath, folderName);
	}

	private Download buildDownload(String fullPath, StreamContent sc) {
		return new Download(fullPath, sc.getSize(), sc.getInputStream());
	}

	private ShowThumbnail buildThumbnailView(String fullPath) throws C5CException {
		Dimension dim = UserObjectProxy.getThumbnailDimension();
		// TODO calling the cache, see issue#27
		StreamContent sc = connector.buildThumbnail(fullPath, dim);
		return new ShowThumbnail(fullPath, sc.getSize(), sc.getInputStream());
	}
	
	private Prieview buildPrieview(String fullPath, StreamContent sc) {
		return new Prieview(fullPath, sc.getSize(), sc.getInputStream());
	}
	
	private void setCapabilities(FileInfo fi, String urlPath) {
		fi.setCapabilities(UserObjectProxy.getC5FileCapabilities(urlPath));
	}
	
	private String encode(String str) {
		try {
			return URLEncoder.encode(str, PropertiesLoader.getConnectorDefaultEncoding());
		} catch (UnsupportedEncodingException e) {
			return "--unsupportedencoding--";
		}
	}
}
