//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.plugins.decrypter;

import java.util.ArrayList;
import java.util.regex.Pattern;

import jd.PluginWrapper;
import jd.controlling.ProgressController;
import jd.parser.Regex;
import jd.plugins.CryptedLink;
import jd.plugins.DecrypterPlugin;
import jd.plugins.DownloadLink;
import jd.plugins.FilePackage;
import jd.plugins.PluginForDecrypt;
import jd.plugins.decrypter.TbCm.DestinationFormat;

@DecrypterPlugin(revision = "$Revision$", interfaceVersion = 2, names = { "clipfish.de" }, urls = { "http://(www\\.)?clipfish\\.de/(.*?channel/\\d+/video/\\d+|video/\\d+(/.+)?|special/.*?/video/\\d+|musikvideos/video/\\d+(/.+)?)" }, flags = { 0 })
public class ClpfshD extends PluginForDecrypt {

    private static final Pattern PATTERN_CAHNNEL_VIDEO  = Pattern.compile("http://(www\\.)?clipfish\\.de/.*?channel/\\d+/video/(\\d+)");
    private static final Pattern PATTERN_MUSIK_VIDEO    = Pattern.compile("http://(www\\.)?clipfish\\.de/musikvideos/video/(\\d+)(/.+)?");
    private static final Pattern PATTERN_STANDARD_VIDEO = Pattern.compile("http://(www\\.)?clipfish\\.de/video/(\\d+)(/.+)?");
    private static final Pattern PATTERN_SPECIAL_VIDEO  = Pattern.compile("http://(www\\.)?clipfish\\.de/special/.*?/video/(\\d+)");
    private static final Pattern PATTERN_FLV_FILE       = Pattern.compile("&url=(http://.+?\\....)&", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_TITEL          = Pattern.compile("<meta property=\"og:title\" content=\"(.+?)\"/>", Pattern.CASE_INSENSITIVE);
    private static final String  XML_PATH               = "http://www.clipfish.de/video_n.php?vid=";

    // private final String NEW_XMP_PATH =
    // "http://www.clipfish.de/devxml/videoinfo/" + vidId + "?ts="+
    // System.currentTimeMillis();

    public ClpfshD(final PluginWrapper wrapper) {
        super(wrapper);
    }

    private void addLink(final CryptedLink cryptedLink, final ArrayList<DownloadLink> decryptedLinks, final String name, final DownloadLink downloadLink, final DestinationFormat convertTo) {
        final FilePackage filePackage = FilePackage.getInstance();
        filePackage.setName("ClipFish " + convertTo.getText() + "(" + convertTo.getExtFirst() + ")");
        filePackage.add(downloadLink);
        downloadLink.setFinalFileName(name + ".tmp");
        downloadLink.setBrowserUrl(cryptedLink.getCryptedUrl());
        downloadLink.setSourcePluginComment("Convert to " + convertTo.getText());
        downloadLink.setProperty("convertto", convertTo.name());
        decryptedLinks.add(downloadLink);
    }

    @Override
    public ArrayList<DownloadLink> decryptIt(final CryptedLink cryptedLink, final ProgressController progress) throws Exception {
        final ArrayList<DownloadLink> decryptedLinks = new ArrayList<DownloadLink>();
        br.clearCookies(getHost());
        final Regex regexInfo = new Regex(br.getPage(cryptedLink.getCryptedUrl()), ClpfshD.PATTERN_TITEL);
        final String tmpStr = regexInfo.getMatch(0);
        if (tmpStr == null) { return null; }
        final String name = tmpStr.substring(0, tmpStr.lastIndexOf("-"));
        final String cType = tmpStr.substring(tmpStr.lastIndexOf("-") + 1, tmpStr.length()).toLowerCase();
        if (name == null || cType == null) { return null; }

        int vidId = -1;
        if (new Regex(cryptedLink.getCryptedUrl(), ClpfshD.PATTERN_STANDARD_VIDEO).matches()) {
            vidId = Integer.parseInt(new Regex(cryptedLink.getCryptedUrl(), ClpfshD.PATTERN_STANDARD_VIDEO).getMatch(0));
        } else if (new Regex(cryptedLink.getCryptedUrl(), ClpfshD.PATTERN_CAHNNEL_VIDEO).matches()) {
            vidId = Integer.parseInt(new Regex(cryptedLink.getCryptedUrl(), ClpfshD.PATTERN_CAHNNEL_VIDEO).getMatch(0));
        } else if (new Regex(cryptedLink.getCryptedUrl(), ClpfshD.PATTERN_SPECIAL_VIDEO).matches()) {
            vidId = Integer.parseInt(new Regex(cryptedLink.getCryptedUrl(), ClpfshD.PATTERN_SPECIAL_VIDEO).getMatch(0));
        } else if (new Regex(cryptedLink.getCryptedUrl(), ClpfshD.PATTERN_MUSIK_VIDEO).matches()) {
            vidId = Integer.parseInt(new Regex(cryptedLink.getCryptedUrl(), ClpfshD.PATTERN_MUSIK_VIDEO).getMatch(0));
        } else {
            logger.severe("No VidID found");
            return decryptedLinks;
        }
        final String page = br.getPage(ClpfshD.XML_PATH + vidId);
        final String pathToflv = new Regex(page, ClpfshD.PATTERN_FLV_FILE).getMatch(0);
        if (pathToflv == null) { return null; }
        final DownloadLink downloadLink = createDownloadlink(pathToflv);
        /*
         * scheinbar gibt es auf clipfish keine flv Audiodateien mehr.
         */
        if (cType.equals("audio")) {
            addLink(cryptedLink, decryptedLinks, name, downloadLink, DestinationFormat.VIDEOFLV);
            addLink(cryptedLink, decryptedLinks, name, downloadLink, DestinationFormat.AUDIOMP3);
        } else {
            String ext = pathToflv.substring(pathToflv.lastIndexOf(".") + 1, pathToflv.length());
            ext = ext.equals("f4v") || ext.equals("") || ext.equals(null) ? "mp4" : ext;
            downloadLink.setFinalFileName(name + "." + ext);
            decryptedLinks.add(downloadLink);
        }

        return decryptedLinks;
    }

}
