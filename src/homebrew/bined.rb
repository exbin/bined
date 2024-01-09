cask "bined" do
  version "0.2.3"
  sha256 "ef16ef1583fb93251dfa1bef1527727bfdb5562753d66df764cf0a851c60e97b"

  url "https://bined.exbin.org/download/bined-#{version}.dmg"
  name "BinEd"
  desc "Free and open source hex viewer/editor written in Java"
  homepage "https://bined.exbin.org/editor"
  
  livecheck do
    url "https://bined.exbin.org/update/homebrew"
    regex(/^v?(\d+(?:\.\d+)+)$/i)
  end

  app "BinEd.app"

  zap trash: "~/Library/Preferences/org.exbin.bined.plist"
end
