//
//  iPodCommand.m
//  HelloPhoneGap
//
//  Created by Hiedi Utley on 4/1/11.
//  Copyright 2011 Chariot Solutions, LLC. All rights reserved.
//

#import "MediaLibrary.h"
#import <MediaPlayer/MediaPlayer.h>
#import <AVFoundation/AVAudioSession.h>

@implementation MediaLibrary

@synthesize player, isMediaSelected;

-(void)isSupported:(CDVInvokedUrlCommand *)command
{
    NSDictionary* returnObj = [NSDictionary dictionaryWithObject:@"true" forKey:@"isSupported"];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)initialize:(CDVInvokedUrlCommand *)command
{
    NSArray* songs = [NSArray array];
    NSDictionary* returnObj = [NSDictionary dictionaryWithObject:songs forKey:@"songs"];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)initPlayer
{
    if(self.player == nil){
        [[AVAudioSession sharedInstance] setCategory:AVAudioSessionCategoryAmbient error:nil];
        self.player = [MPMusicPlayerController systemMusicPlayer];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(musicPlayerPlayBackStatusChanged:)
                                                     name:MPMusicPlayerControllerPlaybackStateDidChangeNotification
                                                   object:nil];
    }
}

// Configures and displays the media item picker.
- (void) showMediaPicker :(CDVInvokedUrlCommand *)command {
    
    [self initPlayer];
    
	MPMediaPickerController *picker = [[MPMediaPickerController alloc] initWithMediaTypes: MPMediaTypeMusic];
	
    if (picker)
    {
        picker.delegate						= self;
        picker.allowsPickingMultipleItems	= YES;
        picker.prompt						= @"Select music to play.";
        
        UIWindow *window = [UIApplication sharedApplication].keyWindow;
        UIViewController *rootViewController = window.rootViewController;
        
        [rootViewController presentViewController: picker animated: YES completion:nil ];
        
    }
    else
    {
        UIAlertView * alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"You must be running on a device for this to work!" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil];
        
        [alert show];
    }
}


// Responds to the user tapping Done after choosing music.
- (void) mediaPicker: (MPMediaPickerController *) mediaPicker didPickMediaItems: (MPMediaItemCollection *) mediaItemCollection {
    
    [self initPlayer];
    
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UIViewController *rootViewController = window.rootViewController;
    
    [rootViewController dismissViewControllerAnimated:YES completion:nil ];
    
    [self.player setQueueWithItemCollection:mediaItemCollection];
    //[self.player play];
    self.isMediaSelected = YES;
}


// Responds to the user tapping done having chosen no music.
- (void) mediaPickerDidCancel: (MPMediaPickerController *) mediaPicker {
    
    [self initPlayer];
    
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    UIViewController *rootViewController = window.rootViewController;
    [rootViewController dismissViewControllerAnimated:YES completion:nil ];
}

- (void) play:(CDVInvokedUrlCommand *)command {
    [self initPlayer];
    if(self.isMediaSelected)[self.player play];
}

- (void) pause:(CDVInvokedUrlCommand *)command {
    [self initPlayer];
    if(self.isMediaSelected)[self.player pause];
}

-(void)musicPlayerPlayBackStatusChanged:(NSNotification *)notification
{
    //NSLog(@"%ld", self.player.playbackState);
}


-(void)getArtists:(CDVInvokedUrlCommand *)command
{
    MPMediaQuery *allArtistsQuery = [MPMediaQuery artistsQuery];
    NSArray *allArtistsArray = [allArtistsQuery  collections];
    
    NSMutableArray *artistsObj = [NSMutableArray arrayWithCapacity:[allArtistsArray count]];
    
    for (MPMediaItemCollection *collection in allArtistsArray) {
        MPMediaItem *item = [collection representativeItem];
        NSDictionary *artistObj = [NSDictionary dictionaryWithObjectsAndKeys:
                                   [[item valueForProperty: MPMediaItemPropertyArtistPersistentID] stringValue], @"id"
                                   ,[item valueForProperty: MPMediaItemPropertyArtist], @"name"
                                   , nil];
        [artistsObj addObject:artistObj];
        
    }
    
    NSDictionary *returnObj = [NSDictionary dictionaryWithObject:artistsObj forKey:@"artists"];
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)getAlbumsByArtist:(CDVInvokedUrlCommand *)command
{
    unsigned long long ullvalue = strtoull([[command.arguments objectAtIndex:0] UTF8String], NULL, 0);
    NSNumber *artistId = [[NSNumber alloc] initWithUnsignedLongLong:ullvalue];
    
    MPMediaPropertyPredicate *artistPredicate = [MPMediaPropertyPredicate predicateWithValue:artistId
                                                                                 forProperty:MPMediaItemPropertyArtistPersistentID
                                  comparisonType:MPMediaPredicateComparisonEqualTo];
    
    MPMediaQuery *albumsByArtistsQuery = [MPMediaQuery albumsQuery];
    [albumsByArtistsQuery addFilterPredicate: artistPredicate];
    
    NSArray *artistAlbumsArray = [albumsByArtistsQuery  collections];
    
    NSMutableArray *albumsObj = [NSMutableArray arrayWithCapacity:[artistAlbumsArray count]];
    for (MPMediaItemCollection *collection in artistAlbumsArray) {
        MPMediaItem *item = [collection representativeItem];
        NSDictionary *albumObj = [NSDictionary dictionaryWithObjectsAndKeys:
                                   [[item valueForProperty: MPMediaItemPropertyAlbumPersistentID] stringValue], @"id"
                                   ,[item valueForProperty: MPMediaItemPropertyAlbumTitle], @"title"
                                   ,[[item valueForProperty: MPMediaItemPropertyArtistPersistentID] stringValue], @"artistId"
                                   ,[item valueForProperty: MPMediaItemPropertyArtist], @"artist"
                                   //,[item valueForProperty: MPMediaItemPropertyArtwork], @"image"
                                   , nil];
        [albumsObj addObject:albumObj];
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObject:albumsObj forKey:@"albums"];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)getSongsByAlbum:(CDVInvokedUrlCommand *)command
{
    unsigned long long ullvalue = strtoull([[command.arguments objectAtIndex:0] UTF8String], NULL, 0);
    NSNumber *albumId = [[NSNumber alloc] initWithUnsignedLongLong:ullvalue];
    
    MPMediaPropertyPredicate *albumPredicate = [MPMediaPropertyPredicate predicateWithValue:albumId
                                                                                 forProperty:MPMediaItemPropertyAlbumPersistentID
                                                                              comparisonType:MPMediaPredicateComparisonEqualTo];
    
    MPMediaQuery *songsByAlbumQuery = [MPMediaQuery songsQuery];
    [songsByAlbumQuery addFilterPredicate: albumPredicate];
    
    NSArray *albumSongsArray = [songsByAlbumQuery collections];
    
    NSMutableArray *songsObj = [NSMutableArray arrayWithCapacity:[albumSongsArray count]];
    for (MPMediaItemCollection *collection in albumSongsArray) {
        MPMediaItem *item = [collection representativeItem];
        NSDictionary *songObj = [NSDictionary dictionaryWithObjectsAndKeys:
                                  [[item valueForProperty: MPMediaItemPropertyPersistentID] stringValue], @"id"
                                 ,[item valueForProperty: MPMediaItemPropertyTitle], @"title"
                                 ,[item valueForProperty: MPMediaItemPropertyAlbumTitle], @"album"
                                 ,[[item valueForProperty: MPMediaItemPropertyAlbumPersistentID] stringValue], @"albumId"
                                 ,[item valueForProperty: MPMediaItemPropertyArtist], @"artist"
                                 ,[[item valueForProperty: MPMediaItemPropertyArtistPersistentID] stringValue], @"artistId"
                                 ,[[item valueForProperty: MPMediaItemPropertyAssetURL] absoluteString], @"url"
                                 ,[item valueForProperty: MPMediaItemPropertyPlaybackDuration], @"duration"
                                  , nil];
        [songsObj addObject:songObj];
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObject:songsObj forKey:@"songs"];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)getAlbums:(CDVInvokedUrlCommand *)command
{
    MPMediaQuery *allAlbumsQuery = [MPMediaQuery albumsQuery];
    NSArray *allAlbumsArray = [allAlbumsQuery  collections];
    
    NSMutableArray *albumsObj = [NSMutableArray arrayWithCapacity:[allAlbumsArray count]];
    
    for (MPMediaItemCollection *collection in allAlbumsArray) {
        MPMediaItem *item = [collection representativeItem];
        NSDictionary *albumObj = [NSDictionary dictionaryWithObjectsAndKeys:
                                  [[item valueForProperty: MPMediaItemPropertyAlbumPersistentID] stringValue], @"id"
                                  ,[item valueForProperty: MPMediaItemPropertyAlbumTitle], @"title"
                                  ,[[item valueForProperty: MPMediaItemPropertyArtistPersistentID] stringValue], @"artistId"
                                  ,[item valueForProperty: MPMediaItemPropertyArtist], @"artist"
                                  //,[item valueForProperty: MPMediaItemPropertyArtwork], @"image"
                                  , nil];
        [albumsObj addObject:albumObj];
        
    }
    
    NSDictionary *returnObj = [NSDictionary dictionaryWithObject:albumsObj forKey:@"albums"];
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)getPlaylists:(CDVInvokedUrlCommand *)command
{
    MPMediaQuery *allPlaylistsQuery = [MPMediaQuery playlistsQuery];
    NSArray *allPlaylistsArray = [allPlaylistsQuery  collections];
    
    NSMutableArray *playlistsObj = [NSMutableArray arrayWithCapacity:[allPlaylistsArray count]];
    
    for (MPMediaPlaylist *collection in allPlaylistsArray) {
        
        NSDictionary *playlistObj = [NSDictionary dictionaryWithObjectsAndKeys:
                                  [[collection valueForProperty: MPMediaPlaylistPropertyPersistentID] stringValue], @"id"
                                  ,[collection valueForProperty: MPMediaPlaylistPropertyName], @"title"
                                  , nil];
        [playlistsObj addObject:playlistObj];
        
    }
    
    NSDictionary *returnObj = [NSDictionary dictionaryWithObject:playlistsObj forKey:@"playlists"];
    
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)getSongsByPlaylist:(CDVInvokedUrlCommand *)command
{
    unsigned long long ullvalue = strtoull([[command.arguments objectAtIndex:0] UTF8String], NULL, 0);
    NSNumber *playlistId = [[NSNumber alloc] initWithUnsignedLongLong:ullvalue];
    
    MPMediaPropertyPredicate *playlistPredicate = [MPMediaPropertyPredicate predicateWithValue:playlistId
                                                                                forProperty:MPMediaItemPropertyPersistentID
                                                                             comparisonType:MPMediaPredicateComparisonEqualTo];
    
    MPMediaQuery *playlistsQuery = [MPMediaQuery playlistsQuery];
    [playlistsQuery addFilterPredicate: playlistPredicate];
    
    NSMutableArray *songsObj = [NSMutableArray array];
    
    NSArray *listsArray = [playlistsQuery collections];
    
    if(listsArray.count > 0)
    {
        MPMediaPlaylist *list = listsArray[0];
        NSArray *playlistSongs = [list items];
        
        for (MPMediaItemCollection *collection in playlistSongs) {
            MPMediaItem *item = [collection representativeItem];
            NSDictionary *songObj = [NSDictionary dictionaryWithObjectsAndKeys:
                                 [[item valueForProperty: MPMediaItemPropertyPersistentID] stringValue], @"id"
                                 ,[item valueForProperty: MPMediaItemPropertyTitle], @"title"
                                 ,[item valueForProperty: MPMediaItemPropertyAlbumTitle], @"album"
                                 ,[[item valueForProperty: MPMediaItemPropertyAlbumPersistentID] stringValue], @"albumId"
                                 ,[item valueForProperty: MPMediaItemPropertyArtist], @"artist"
                                 ,[[item valueForProperty: MPMediaItemPropertyArtistPersistentID] stringValue], @"artistId"
                                 ,[[item valueForProperty: MPMediaItemPropertyAssetURL] absoluteString], @"url"                                 
                                 ,[item valueForProperty: MPMediaItemPropertyPlaybackDuration], @"duration"
                                 ,[playlistId stringValue], @"playlistId"
                                 , nil];
            [songsObj addObject:songObj];
        }
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObject:songsObj forKey:@"songs"];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

-(void)getSongFiles:(CDVInvokedUrlCommand *)command
{
    MPMediaQuery *songsQuery = [MPMediaQuery songsQuery];
    NSArray *songsArray = [songsQuery collections];
    
    NSMutableArray *songsObj = [NSMutableArray arrayWithCapacity:[songsArray count]];
    for (MPMediaItemCollection *collection in songsArray) {
        MPMediaItem *item = [collection representativeItem];
        NSDictionary *songObj = [NSDictionary dictionaryWithObjectsAndKeys:
                                 [[item valueForProperty: MPMediaItemPropertyPersistentID] stringValue], @"id"
                                 ,[item valueForProperty: MPMediaItemPropertyTitle], @"title"
                                 ,[item valueForProperty: MPMediaItemPropertyAlbumTitle], @"album"
                                 ,[[item valueForProperty: MPMediaItemPropertyAlbumPersistentID] stringValue], @"albumId"
                                 ,[item valueForProperty: MPMediaItemPropertyArtist], @"artist"
                                 ,[[item valueForProperty: MPMediaItemPropertyArtistPersistentID] stringValue], @"artistId"
                                 ,[[item valueForProperty: MPMediaItemPropertyAssetURL] absoluteString], @"url"
                                 ,[item valueForProperty: MPMediaItemPropertyPlaybackDuration], @"duration"
                                 , nil];
        [songsObj addObject:songObj];
    }
    
    NSDictionary* returnObj = [NSDictionary dictionaryWithObject:songsObj forKey:@"songs"];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}


@end
