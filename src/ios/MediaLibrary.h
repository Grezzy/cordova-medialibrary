//
//  iPodCommand.h
//  HelloPhoneGap
//
//  Created by Hiedi Utley on 4/1/11.
//  Copyright 2011 Chariot Solutions, LLC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>
#import <MediaPlayer/MediaPlayer.h>

@interface MediaLibrary : CDVPlugin <MPMediaPickerControllerDelegate, UITableViewDelegate> {
        
    
}

@property BOOL isMediaSelected;
@property (nonatomic, retain) MPMusicPlayerController * player;

- (void) isSupported:(CDVInvokedUrlCommand *)command;
- (void) initialize:(CDVInvokedUrlCommand *)command;
- (void) showMediaPicker:(CDVInvokedUrlCommand *)command;
- (void) play:(CDVInvokedUrlCommand *)command;
- (void) pause:(CDVInvokedUrlCommand *)command;
- (void) getArtists:(CDVInvokedUrlCommand *)command;
- (void) getAlbumsByArtist:(CDVInvokedUrlCommand *)command;
- (void) getSongsByAlbum:(CDVInvokedUrlCommand *)command;
- (void) getAlbums:(CDVInvokedUrlCommand *)command;
- (void) getPlaylists:(CDVInvokedUrlCommand *)command;
- (void) getSongsByPlaylist:(CDVInvokedUrlCommand *)command;
- (void) getSongFiles:(CDVInvokedUrlCommand *)command;

@end
